package site.praytogether.pray_together.domain.room.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomMemberResponse {
  private final List<RoomMemberDto> members;
}
