package site.praytogether.pray_together.domain.prayer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrayerContentInfo {
  private Long id;
  private Long writerId;
  private String writerName;
  private Long memberId;
  private String memberName;
  private String content;
}
