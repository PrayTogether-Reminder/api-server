package site.praytogether.pray_together.domain.prayer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrayerCompletionCreateRequest {
  @NotNull(message = "잘 못된 기도제목 입니다.")
  @Positive(message = "잘 못된 기도제목 입니다.")
  private final Long roomId;
}
