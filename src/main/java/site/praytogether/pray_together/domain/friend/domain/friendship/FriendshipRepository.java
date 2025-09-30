package site.praytogether.pray_together.domain.friend.domain.friendship;

import java.util.List;

public interface FriendshipRepository {

  boolean isExist(Long inviter, Long invitee);
  void save(Friendship friendship);
  List<Friendship> getFriendshipList(Long memberId);
}
