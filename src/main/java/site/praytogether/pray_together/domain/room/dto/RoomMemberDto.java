package site.praytogether.pray_together.domain.room.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RoomMemberDto {
  Long id;
  String name;
  String phoneNumberSuffix;
}
