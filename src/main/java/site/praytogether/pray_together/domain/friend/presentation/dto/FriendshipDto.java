package site.praytogether.pray_together.domain.friend.presentation.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FriendshipDto {
  String friendName;
  long friendId;
}
