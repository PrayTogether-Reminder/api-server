package site.praytogether.pray_together.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.notification.constant.NotificationMessageFormat;
import site.praytogether.pray_together.domain.notification.model.FcmToken;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationGateway {
  private final FirebaseMessaging firebaseMessaging;

  public void notifyCompletePrayer(List<FcmToken> fcmTokens, String senderName) {
    fcmTokens.forEach(token -> sendCompletePrayerMessage(token, senderName));
  }

  private void sendCompletePrayerMessage(FcmToken token, String senderName) {
    sendMessage(
        token.getToken(),
        "기도 완료 알림",
        String.format(NotificationMessageFormat.PrayerCompletion, senderName));
  }

  public void sendMessage(String token, String title, String body) {
    Notification fcmNotification = Notification.builder().setTitle(title).setBody(body).build();
    Message fcmMessage = Message.builder().setToken(token).setNotification(fcmNotification).build();
    try {
      String send = firebaseMessaging.send(fcmMessage);
      log.info("FCM 전송 성공 : {}", send);
    } catch (FirebaseMessagingException e) {
      log.error("FCM 전송 실패 message={}", body);
      throw new RuntimeException(e);
    }
  }
}
