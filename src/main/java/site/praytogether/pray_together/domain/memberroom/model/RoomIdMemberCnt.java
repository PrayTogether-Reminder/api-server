package site.praytogether.pray_together.domain.memberroom.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomIdMemberCnt {
  Long roomId;
  Long memberCnt;

  public RoomIdMemberCnt(Long id, Long memberCnt) {
    this.roomId = id;
    this.memberCnt = memberCnt;
  }
}
