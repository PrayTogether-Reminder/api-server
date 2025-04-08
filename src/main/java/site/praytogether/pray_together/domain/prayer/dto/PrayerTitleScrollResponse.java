package site.praytogether.pray_together.domain.prayer.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitleInfo;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerTitleScrollResponse {
  private final List<PrayerTitleInfo> prayerTitles;

  public static PrayerTitleScrollResponse from(List<PrayerTitleInfo> prayerTitles) {
    return new PrayerTitleScrollResponse(prayerTitles);
  }
}
