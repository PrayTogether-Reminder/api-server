package site.praytogether.pray_together.domain.prayer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerUpdateContent {
  @NotNull(message = "올바르지 않은 기도 내용 입니다.")
  @Positive(message = "올바으지 않은 기도 내용 입니다.")
  private final Long id;

  private final Long memberId;

  @NotBlank(message = "기도 대상자 이름을 작성해 주세요.")
  private final String memberName;

  @NotBlank(message = "기도 내용을 입력해 주세요.")
  private final String content;
}
