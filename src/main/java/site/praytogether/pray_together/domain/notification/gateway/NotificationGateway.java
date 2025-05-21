package site.praytogether.pray_together.domain.notification.gateway;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationGateway {
  private final FirebaseMessaging firebaseMessaging;

  public void notifyCompletePrayer(List<FcmToken> fcmTokens, String message) {
    fcmTokens.forEach(token -> sendCompletePrayerMessage(token, message));
  }

  private void sendCompletePrayerMessage(FcmToken token, String message) {
    sendMessage(token.getToken(), "기도 완료 알림", message);
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
