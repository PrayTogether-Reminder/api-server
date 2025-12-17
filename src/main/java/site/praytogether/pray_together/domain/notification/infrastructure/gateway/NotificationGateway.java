package site.praytogether.pray_together.domain.notification.infrastructure.gateway;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.fcm_token.service.FcmTokenService;
import site.praytogether.pray_together.domain.notification.domain.PrayerCompletionNotificationCommand;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationGateway {
  private static final String PRAYER_COMPLETION_TITLE = "기도 완료 알림";

  private final FirebaseMessaging firebaseMessaging;
  private final FcmTokenService fcmTokenService;

  public void notifyPrayerCompletion(PrayerCompletionNotificationCommand command) {
    command.getFcmTokens().forEach(token -> sendPrayerCompletionMessage(token, command));
  }

  private void sendPrayerCompletionMessage(
      FcmToken token, PrayerCompletionNotificationCommand command) {
    PrayerTitle prayerTitle = command.getPrayerTitle();
    Notification fcmNotification =
        Notification.builder()
            .setTitle(PRAYER_COMPLETION_TITLE)
            .setBody(command.getMessage())
            .build();
    Message fcmMessage =
        Message.builder()
            .setToken(token.getToken())
            .setNotification(fcmNotification)
            .putData("Title", PRAYER_COMPLETION_TITLE)
            .putData("Body", command.getMessage())
            .putData("roomId", String.valueOf(command.getRoomId()))
            .putData("prayerTitle", prayerTitle.getTitle())
            .putData("prayerTitleId", String.valueOf(prayerTitle.getId()))
            .build();
    try {
      log.info("[FCM] 기도 완료 전송 시작 roomId={} titleId={}",
          command.getRoomId(), prayerTitle.getId());
      firebaseMessaging.send(fcmMessage);
      log.info("[FCM] 기도 완료 전송 성공 roomId={} titleId={}",
          command.getRoomId(), prayerTitle.getId());
    } catch (FirebaseMessagingException e) {
      log.error("[FCM] 기도 완료 전송 실패 roomId={} titleId={}",
          command.getRoomId(), prayerTitle.getId());
      fcmTokenService.deleteByToken(token.getToken());
    }
  }
}
