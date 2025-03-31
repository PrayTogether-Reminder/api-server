package site.praytogether.pray_together.domain.memberroom.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor()
@Getter
public class RoomInfo {
  private final Long roomId;
  private final String name;
  private Long memberCnt;
  private final String description;
  private final Instant joinedTime;
  private final boolean isNotification;

  public RoomInfo(
      Long id, String name, String description, Instant createdTime, boolean isNotification) {
    this.roomId = id;
    this.name = name;
    this.memberCnt = 0L;
    this.description = description;
    this.joinedTime = createdTime;
    this.isNotification = isNotification;
  }

  public void setMemberCnt(Long memberCnt) {
    this.memberCnt = memberCnt;
  }
}
