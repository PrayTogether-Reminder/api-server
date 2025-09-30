package site.praytogether.pray_together.domain.friend.application.mapper;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;
import site.praytogether.pray_together.domain.friend.presentation.dto.FriendshipDto;
import site.praytogether.pray_together.domain.member.model.Member;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendshipMapper {

  public static FriendshipDto toDto(Friendship friendship, Member member) {
    Member friend = friendship.getFriendBy(member);
    return FriendshipDto.builder()
        .friendId(friend.getId())
        .friendName(friend.getName())
        .build();
  }

  public static List<FriendshipDto> toDtos(List<Friendship> friendships, Member member) {
    return friendships.stream()
        .map(friendship -> toDto(friendship, member))
        .toList();
  }
}
