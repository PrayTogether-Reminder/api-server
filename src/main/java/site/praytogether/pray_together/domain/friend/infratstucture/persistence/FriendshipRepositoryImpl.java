package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.praytogether.pray_together.domain.friend.domain.FriendshipRepository;

@Repository
@RequiredArgsConstructor
public class FriendshipRepositoryImpl implements FriendshipRepository {
  private final FriendshipJpaRepository friendshipJpaRepository;
}
