package site.praytogether.pray_together.domain.prayer.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerTitleInfiniteScrollRequest {
  private Long roomId;
  private String after;

  public static PrayerTitleInfiniteScrollRequest of(Long roomId, String after) {
    return new PrayerTitleInfiniteScrollRequest(roomId, after);
  }
}
