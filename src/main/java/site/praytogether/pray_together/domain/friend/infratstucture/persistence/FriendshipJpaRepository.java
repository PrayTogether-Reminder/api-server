package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;

public interface FriendshipJpaRepository extends JpaRepository<Friendship,Long> {
  boolean existsByMember1_IdAndMember2_Id(Long member1, Long member2);
}
