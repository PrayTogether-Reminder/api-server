package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;

public interface FriendInvitationJpaRepository extends JpaRepository<FriendInvitation, Long> {

}
