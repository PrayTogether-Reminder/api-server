package site.praytogether.pray_together.domain.prayer.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class PrayerCompletionCountDto {
  Long memberId;
  String memberName;
  Long prayerCount;
}
