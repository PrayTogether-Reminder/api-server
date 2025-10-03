package site.praytogether.pray_together.domain.friend.infratstucture.persistence;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationRepository;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationStatus;

@Repository
@RequiredArgsConstructor
public class FriendInvitationRepositoryImpl implements FriendInvitationRepository {
  private final FriendInvitationJpaRepository jpaRepository;

  @Override
  public FriendInvitation save(FriendInvitation friendInvitation) {
    return jpaRepository.save(friendInvitation);
  }

  @Override
  public List<FriendInvitation> findAllByReceiver_IdAndStatus(Long memberId, FriendInvitationStatus status) {
    return jpaRepository.findAllByReceiver_IdAndStatus(memberId,status);
  }

  @Override
  public Optional<FriendInvitation> findByReceiver_IdAndId(Long receiverId,Long invitationId) {
    return jpaRepository.findByReceiver_IdAndId(receiverId, invitationId);
  }

  @Override
  public Optional<FriendInvitation> findBySender_IdAndReceiver_IdAndStatus(Long senderId, Long receiverId, FriendInvitationStatus status) {
    return jpaRepository.findBySender_IdAndReceiver_IdAndStatus(senderId, receiverId, status);
  }
}
