package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationStatus;

public interface FriendInvitationJpaRepository extends JpaRepository<FriendInvitation, Long> {

  List<FriendInvitation> findAllByReceiver_IdAndStatus(Long senderId, FriendInvitationStatus status);
}
