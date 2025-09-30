package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;
import site.praytogether.pray_together.domain.friend.domain.friendship.FriendshipRepository;

@Repository
@RequiredArgsConstructor
public class FriendshipRepositoryImpl implements FriendshipRepository {
  private final FriendshipJpaRepository jpaRepository;

  @Override
  public boolean isExist(Long memberId1, Long memberId2) {
    return jpaRepository.existsByMember1_IdAndMember2_Id(memberId1,memberId2);
  }

  @Override
  public void save(Friendship friendship) {
    jpaRepository.save(friendship);
    return;
  }

  @Override
  public List<Friendship> getFriendshipList(Long memberId) {
    return jpaRepository.findFriendshipBy(memberId);
  }

  @Override
  public Optional<Friendship> findByMemberIds(Long memberId1, Long memberId2) {
    return jpaRepository.findByMember1_IdAndMember2_Id(memberId1, memberId2);
  }

  @Override
  public void delete(Friendship friendship) {
    jpaRepository.delete(friendship);
  }
}
