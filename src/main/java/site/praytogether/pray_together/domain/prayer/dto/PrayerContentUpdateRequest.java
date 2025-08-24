package site.praytogether.pray_together.domain.prayer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import site.praytogether.pray_together.domain.prayer.model.PrayerUpdateContent;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerContentUpdateRequest {
  @Valid
  @NotNull(message = "기도 내용을 작성해 주세요.")
  private final PrayerUpdateContent content;
}