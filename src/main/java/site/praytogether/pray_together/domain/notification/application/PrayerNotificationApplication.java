package site.praytogether.pray_together.domain.notification.application;

import static site.praytogether.pray_together.domain.notification.domain.NotificationMessageFormat.PrayerCompletion;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.fcm_token.service.FcmTokenService;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.domain.member_room.service.MemberRoomService;
import site.praytogether.pray_together.domain.notification.domain.NotificationResult;
import site.praytogether.pray_together.domain.notification.domain.PrayerCompletionNotificationCommand;
import site.praytogether.pray_together.domain.notification.domain.PrayerCompletionNotificationService;
import site.praytogether.pray_together.domain.notification.infrastructure.gateway.NotificationGateway;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitleService;
import site.praytogether.pray_together.domain.prayer.domain.event.PrayerCompletionEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrayerNotificationApplication {

  private final MemberRoomService memberRoomService;
  private final MemberService memberService;
  private final PrayerTitleService prayerTitleService;
  private final PrayerCompletionNotificationService notificationService;
  private final FcmTokenService fcmTokenService;
  private final NotificationGateway notificationGateway;

  @Transactional
  public void sendPrayerCompletion(PrayerCompletionEvent event) {
    PrayerTitle prayerTitle = prayerTitleService.fetchById(event.getPrayerTitleId());
    List<Long> memberIds = memberRoomService.fetchMemberIdsInRoom(event.getRoomId());
    Member sender = memberService.fetchById(event.getSenderId());
    String message = String.format(PrayerCompletion, sender.getName(), prayerTitle.getTitle());

    List<FcmToken> fcmTokens = fcmTokenService.fetchTokensByMemberIds(memberIds);
    PrayerCompletionNotificationCommand command =
        PrayerCompletionNotificationCommand.of(
            event.getRoomId(), event.getSenderId(), prayerTitle, message, fcmTokens);

    NotificationResult result = notificationGateway.notifyPrayerCompletion(command);

    // 비즈니스 정책: 실패한 토큰은 즉시 삭제
    if (result.hasFailed()) {
      fcmTokenService.deleteByTokens(result.getFailedTokens());
      log.info(
          "[Notification] FCM 실패 토큰 삭제 완료 - 성공: {}, 실패: {}",
          result.getSuccessCount(), result.getFailedCount());
    }

    notificationService.create(event.getSenderId(), memberIds, message, prayerTitle);
  }
}
