package site.praytogether.pray_together.domain.prayer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import site.praytogether.pray_together.domain.prayer.model.PrayerRequestContent;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerContentCreateRequest {
  @Valid
  @NotNull(message = "기도 내용을 작성해 주세요.")
  private final PrayerRequestContent content;
}