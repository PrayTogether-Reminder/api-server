package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.praytogether.pray_together.domain.friend.domain.FriendInvitationRepository;

@Repository
@RequiredArgsConstructor
public class FriendInvitationRepositoryImpl implements FriendInvitationRepository {
  private final FriendInvitationJpaRepository friendInvitationJpaRepository;
}
