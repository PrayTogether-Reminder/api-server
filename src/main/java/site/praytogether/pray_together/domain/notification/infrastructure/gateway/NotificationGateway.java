package site.praytogether.pray_together.domain.notification.infrastructure.gateway;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.notification.domain.NotificationResult;
import site.praytogether.pray_together.domain.notification.domain.PrayerCompletionNotificationCommand;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationGateway {
  private static final String PRAYER_COMPLETION_TITLE = "기도 완료 알림";

  private final FirebaseMessaging firebaseMessaging;

  public NotificationResult notifyPrayerCompletion(PrayerCompletionNotificationCommand command) {
    List<FcmToken> successTokens = new ArrayList<>();
    List<FcmToken> failedTokens = new ArrayList<>();

    command
        .getFcmTokens()
        .forEach(
            token -> {
              boolean success = sendPrayerCompletionMessage(token, command);
              if (success) {
                successTokens.add(token);
              } else {
                failedTokens.add(token);
              }
            });

    return NotificationResult.of(successTokens, failedTokens);
  }

  private boolean sendPrayerCompletionMessage(
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
      log.info(
          "[FCM] 기도 완료 전송 시작 roomId={} titleId={}", command.getRoomId(), prayerTitle.getId());
      firebaseMessaging.send(fcmMessage);
      log.info(
          "[FCM] 기도 완료 전송 성공 roomId={} titleId={}", command.getRoomId(), prayerTitle.getId());
      return true;
    } catch (FirebaseMessagingException e) {
      log.error(
          "[FCM] 기도 완료 전송 실패 roomId={} titleId={}", command.getRoomId(), prayerTitle.getId());
      return false;
    }
  }
}
