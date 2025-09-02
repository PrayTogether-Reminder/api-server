package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.friend.domain.FriendInvitation;

public interface FriendInvitationJpaRepository extends JpaRepository<FriendInvitation, Long> {

}
