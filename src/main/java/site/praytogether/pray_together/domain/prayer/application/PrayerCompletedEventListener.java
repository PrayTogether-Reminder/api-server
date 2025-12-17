package site.praytogether.pray_together.domain.prayer.application;

import static site.praytogether.pray_together.domain.notification.domain.NotificationMessageFormat.PrayerCompletion;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.fcm_token.service.FcmTokenService;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.domain.member_room.service.MemberRoomService;
import site.praytogether.pray_together.domain.notification.domain.PrayerCompletionNotificationCommand;
import site.praytogether.pray_together.domain.notification.domain.PrayerCompletionNotificationService;
import site.praytogether.pray_together.domain.notification.infrastructure.gateway.NotificationGateway;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitleService;
import site.praytogether.pray_together.domain.prayer.domain.event.PrayerCompletionEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrayerCompletedEventListener {

  private final MemberRoomService memberRoomService;
  private final MemberService memberService;
  private final PrayerTitleService prayerTitleService;
  private final PrayerCompletionNotificationService notificationService;
  private final FcmTokenService fcmTokenService;
  private final NotificationGateway notificationGateway;

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional
  public void handlePrayerCompleted(PrayerCompletionEvent event) {
    try {
      PrayerTitle prayerTitle = prayerTitleService.fetchById(event.getPrayerTitleId());
      List<Long> memberIds = memberRoomService.fetchMemberIdsInRoom(event.getRoomId());
      Member sender = memberService.fetchById(event.getSenderId());
      String message = String.format(PrayerCompletion, sender.getName(), prayerTitle.getTitle());

      List<FcmToken> fcmTokens = fcmTokenService.fetchTokensByMemberIds(memberIds);
      PrayerCompletionNotificationCommand command =
          PrayerCompletionNotificationCommand.of(
              event.getRoomId(), event.getSenderId(), prayerTitle, message, fcmTokens);

      notificationGateway.notifyPrayerCompletion(command);
      notificationService.create(event.getSenderId(), memberIds, message, prayerTitle);
    } catch (Exception e) {
      log.error("[Notification] 기도 완료 알림 처리 실패: {}", e.getMessage(), e);
    }
  }
}
