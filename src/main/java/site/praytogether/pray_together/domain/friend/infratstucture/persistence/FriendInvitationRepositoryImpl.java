package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationRepository;

@Repository
@RequiredArgsConstructor
public class FriendInvitationRepositoryImpl implements FriendInvitationRepository {
  private final FriendInvitationJpaRepository jpaRepository;

  @Override
  public FriendInvitation save(FriendInvitation friendInvitation) {
    return jpaRepository.save(friendInvitation);
  }
}
