package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;

public interface FriendshipJpaRepository extends JpaRepository<Friendship,Long> {
  boolean existsByMember1_IdAndMember2_Id(Long member1, Long member2);

  @Query("""
   SELECT f FROM Friendship f
   JOIN FETCH f.member1
   JOIN FETCH f.member2
    WHERE f.member1.id = :memberId OR f.member2.id = :memberId
""")
  List<Friendship> findFriendshipBy(@Param("memberId") Long memberId);

  Optional<Friendship> findByMember1_IdAndMember2_Id(Long member1Id, Long member2Id);
}