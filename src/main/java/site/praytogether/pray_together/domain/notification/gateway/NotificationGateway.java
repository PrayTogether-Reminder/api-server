package site.praytogether.pray_together.domain.notification.gateway;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationGateway {
  private final FirebaseMessaging firebaseMessaging;

  @Async("notificationExecutor")
  public void notifyCompletePrayer(List<FcmToken> fcmTokens,Long roomId, PrayerTitle prayerTitle, String message, Consumer<String> onInvalidToken) {
    fcmTokens.forEach(token -> sendCompletePrayerMessage(token, roomId, prayerTitle ,message,onInvalidToken));
  }

  private void sendCompletePrayerMessage(FcmToken token,Long roomId, PrayerTitle prayerTitle, String message, Consumer<String> onInvalidToken) {
      sendMessage(token.getToken(), roomId, prayerTitle,"기도 완료 알림",message,onInvalidToken);
  }

  public void sendMessage(String token, Long roomId, PrayerTitle prayerTitle, String title, String body, Consumer<String> onInvalidToken) {
    Notification fcmNotification = Notification.builder().setTitle(title).setBody(body).build();
    Message fcmMessage = Message.builder()
        .setToken(token)
        .setNotification(fcmNotification)
        .putData("Title",title)
        .putData("Body",body)
        .putData("roomId",String.valueOf(roomId))
        .putData("prayerTitle",prayerTitle.getTitle())
        .putData("prayerTitleId",String.valueOf(prayerTitle.getId()))
        .build();
    try {
      log.info("[FCM] 기도 완료 전송 시작 roomId={} titleId={}",roomId,prayerTitle.getId());
      String send = firebaseMessaging.send(fcmMessage);
      log.info("[FCM] 기도 완료 전송 성공 roomId={} titleId={}",roomId,prayerTitle.getId());
    } catch (FirebaseMessagingException e) {
      log.error("[FCM] 기도 완료 전송 실패 roomId={} titleId={}",roomId,prayerTitle.getId());
      onInvalidToken.accept(token);
      log.error("[FCM] 기도 완료 오류 토큰 삭제 성공 roomId={} titleId={}",roomId,prayerTitle.getId());
    }
  }
}
