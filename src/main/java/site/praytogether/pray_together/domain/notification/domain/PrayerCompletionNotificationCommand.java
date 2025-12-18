package site.praytogether.pray_together.domain.notification.domain;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;

@Getter
@Builder
public class PrayerCompletionNotificationCommand {
  private final Long roomId;
  private final Long senderId;
  private final PrayerTitle prayerTitle;
  private final String message;
  private final List<FcmToken> fcmTokens;

  public static PrayerCompletionNotificationCommand of(
      Long roomId,
      Long senderId,
      PrayerTitle prayerTitle,
      String message,
      List<FcmToken> fcmTokens) {
    return PrayerCompletionNotificationCommand.builder()
        .roomId(roomId)
        .senderId(senderId)
        .prayerTitle(prayerTitle)
        .message(message)
        .fcmTokens(fcmTokens)
        .build();
  }
}
