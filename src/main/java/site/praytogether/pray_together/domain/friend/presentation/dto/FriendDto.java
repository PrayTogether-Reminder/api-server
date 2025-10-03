package site.praytogether.pray_together.domain.friend.presentation.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FriendDto {
  long friendId;
  String friendName;
  String friendEmail;
}
