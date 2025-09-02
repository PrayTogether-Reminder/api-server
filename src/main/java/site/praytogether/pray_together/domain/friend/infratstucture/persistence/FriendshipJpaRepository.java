package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.friend.domain.Friendship;

public interface FriendshipJpaRepository extends JpaRepository<Friendship,Long> {

}
