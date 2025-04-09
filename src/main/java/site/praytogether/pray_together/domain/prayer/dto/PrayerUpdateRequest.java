package site.praytogether.pray_together.domain.prayer.dto;

import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.TITLE_MAX_LEN;
import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.TITLE_MIN_LEN;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import site.praytogether.pray_together.domain.prayer.model.PrayerUpdateContent;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerUpdateRequest {
  @NotBlank(message = "기도 제목을 작성해 주세요.")
  @Size(message = "기도 제목은 1자 이상 50자 이하로 작성해 주세요.", min = TITLE_MIN_LEN, max = TITLE_MAX_LEN)
  private final String title;

  @Valid
  @NotNull(message = "기도 내용을 작성해 주세요.")
  private final List<PrayerUpdateContent> contents;
}
