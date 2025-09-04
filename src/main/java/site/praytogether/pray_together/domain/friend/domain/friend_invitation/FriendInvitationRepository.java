package site.praytogether.pray_together.domain.friend.domain.friend_invitation;

import java.util.List;

public interface FriendInvitationRepository {
  FriendInvitation save(FriendInvitation friendInvitation);
  List<FriendInvitation> findAllByReceiver_IdAndStatus(Long senderId,FriendInvitationStatus status);
}
