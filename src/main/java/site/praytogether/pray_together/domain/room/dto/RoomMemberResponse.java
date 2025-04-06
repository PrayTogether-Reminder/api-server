package site.praytogether.pray_together.domain.room.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import site.praytogether.pray_together.domain.member.model.MemberIdName;

@Getter
@AllArgsConstructor
public class RoomMemberResponse {
  private final List<MemberIdName> members;

  public static RoomMemberResponse from(List<MemberIdName> memberIdNames) {
    return new RoomMemberResponse(memberIdNames);
  }
}
