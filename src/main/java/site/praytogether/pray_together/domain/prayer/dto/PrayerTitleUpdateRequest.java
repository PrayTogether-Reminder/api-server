package site.praytogether.pray_together.domain.prayer.dto;

import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.TITLE_MAX_LEN;
import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.TITLE_MIN_LEN;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerTitleUpdateRequest {
  @NotBlank(message = "기도 제목을 작성해 주세요.")
  @Size(message = "기도 제목은 1자 이상 50자 이하로 작성해 주세요.", min = TITLE_MIN_LEN, max = TITLE_MAX_LEN)
  private final String title;
}