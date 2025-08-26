package site.praytogether.pray_together.domain.prayer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerContentUpdateRequest {
  @Valid
  @NotBlank(message = "기도 내용을 입력해 주세요.")
  private final String changedContent;
}