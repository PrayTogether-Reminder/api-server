package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.praytogether.pray_together.domain.friend.domain.friendship.FriendshipRepository;

@Repository
@RequiredArgsConstructor
public class FriendshipRepositoryImpl implements FriendshipRepository {
  private final FriendshipJpaRepository jpaRepository;

  @Override
  public boolean isExist(Long memberId1, Long memberId2) {
    return jpaRepository.existsByMember1_IdAndMember2_Id(memberId1,memberId2);
  }
}
