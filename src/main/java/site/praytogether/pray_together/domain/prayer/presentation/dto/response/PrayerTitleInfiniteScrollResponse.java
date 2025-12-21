package site.praytogether.pray_together.domain.prayer.presentation.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Value;
import site.praytogether.pray_together.domain.prayer.presentation.dto.PrayerTitleInfoDto;

@Getter
@Value
public class PrayerTitleInfiniteScrollResponse {
  List<PrayerTitleInfoDto> prayerTitles;
}
