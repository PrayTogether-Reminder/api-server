package site.praytogether.pray_together.domain.prayer.dto;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerTitleResponse {
  private final Long id;
  private final String title;
  private final Instant createdTime;

  public static PrayerTitleResponse of(Long id, String title, Instant createdTime) {
    return PrayerTitleResponse.builder()
        .id(id)
        .title(title)
        .createdTime(createdTime)
        .build();
  }
}