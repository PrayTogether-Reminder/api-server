package site.praytogether.pray_together.domain.prayer.presentation.dto;

import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
public class PrayerTitleInfoDto {
  Long id;
  String title;
  Instant createdTime;
  List<PrayerCompletionCountDto> prayers;
}
