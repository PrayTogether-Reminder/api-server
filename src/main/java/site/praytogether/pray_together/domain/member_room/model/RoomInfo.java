package site.praytogether.pray_together.domain.member_room.model;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomInfo {
  private Long id;
  private String name;
  private Long memberCnt;
  private String description;
  private Instant joinedTime;
  private boolean isNotification;

  public RoomInfo(
      Long id, String name, String description, Instant createdTime, boolean isNotification) {
    this.id = id;
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
