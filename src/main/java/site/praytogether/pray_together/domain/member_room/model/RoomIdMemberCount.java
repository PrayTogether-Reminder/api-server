package site.praytogether.pray_together.domain.member_room.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomIdMemberCount {
  Long roomId;
  Long memberCnt;

  public RoomIdMemberCount(Long id, Long memberCnt) {
    this.roomId = id;
    this.memberCnt = memberCnt;
  }
}
