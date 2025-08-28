package site.praytogether.pray_together.domain.prayer.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerRequestContent {
  private final Long memberId;

  private final String memberName;

  private final String content;
}
