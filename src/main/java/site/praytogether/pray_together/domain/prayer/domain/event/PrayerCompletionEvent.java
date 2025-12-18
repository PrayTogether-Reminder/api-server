package site.praytogether.pray_together.domain.prayer.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrayerCompletionEvent {

  private final Long senderId;
  private final Long roomId;
  private final Long prayerTitleId;

  public static PrayerCompletionEvent of(Long senderId, Long roomId, Long prayerTitleId) {
    return new PrayerCompletionEvent(senderId, roomId, prayerTitleId);
  }
}
