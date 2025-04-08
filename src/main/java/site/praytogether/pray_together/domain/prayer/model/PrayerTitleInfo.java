package site.praytogether.pray_together.domain.prayer.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrayerTitleInfo {
  private final Long roomId;
  private final String title;
  private final Instant createdTime;
}
