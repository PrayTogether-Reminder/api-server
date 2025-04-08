package site.praytogether.pray_together.domain.prayer.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerScrollRequest {
  private Long roomId;
  private String after;

  public static PrayerScrollRequest of(Long roomId, String after) {
    return new PrayerScrollRequest(roomId, after);
  }
}
