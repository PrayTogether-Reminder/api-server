package site.praytogether.pray_together.domain.friend.domain.friend_invitation;

import java.util.List;
import java.util.Optional;

public interface FriendInvitationRepository {
  FriendInvitation save(FriendInvitation friendInvitation);
  List<FriendInvitation> findAllByReceiver_IdAndStatus(Long senderId,FriendInvitationStatus status);
  Optional<FriendInvitation> findByReceiver_IdAndId(Long receiverId ,Long invitationId);
  Optional<FriendInvitation> findBySender_IdAndReceiver_IdAndStatus(Long senderId, Long receiverId, FriendInvitationStatus status);
}
