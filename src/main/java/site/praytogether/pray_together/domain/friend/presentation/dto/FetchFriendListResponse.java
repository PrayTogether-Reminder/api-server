package site.praytogether.pray_together.domain.friend.presentation.dto;

import java.util.List;
import lombok.Value;

@Value
public class FetchFriendListResponse {
  List<FriendDto> friends;
}
