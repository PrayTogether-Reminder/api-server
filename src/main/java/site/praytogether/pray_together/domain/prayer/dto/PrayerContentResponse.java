package site.praytogether.pray_together.domain.prayer.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerContentResponse {
  private final List<PrayerContentInfo> prayerContents;

  public static PrayerContentResponse from(List<PrayerContentInfo> prayerContents) {
    return new PrayerContentResponse(prayerContents);
  }
}
