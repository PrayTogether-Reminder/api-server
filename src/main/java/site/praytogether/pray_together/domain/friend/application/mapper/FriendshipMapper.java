package site.praytogether.pray_together.domain.friend.application.mapper;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;
import site.praytogether.pray_together.domain.friend.presentation.dto.FriendDto;
import site.praytogether.pray_together.domain.member.model.Member;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FriendshipMapper {

  public static FriendDto toDto(Friendship friendship, Member member) {
    Member friend = friendship.getFriendBy(member);
    return FriendDto.builder()
        .friendId(friend.getId())
        .friendName(friend.getName())
        .friendEmail(friend.getEmail())
        .build();
  }

  public static List<FriendDto> toDtos(List<Friendship> friends, Member member) {
    return friends.stream()
        .map(friendship -> toDto(friendship, member))
        .toList();
  }
}
