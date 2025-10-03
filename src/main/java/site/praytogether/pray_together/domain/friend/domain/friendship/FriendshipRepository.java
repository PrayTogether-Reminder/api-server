package site.praytogether.pray_together.domain.friend.domain.friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository {

  boolean isExist(Long inviter, Long invitee);
  void save(Friendship friendship);
  List<Friendship> getFriendshipList(Long memberId);
  Optional<Friendship> findByMemberIds(Long memberId1, Long memberId2);
  void delete(Friendship friendship);
}
