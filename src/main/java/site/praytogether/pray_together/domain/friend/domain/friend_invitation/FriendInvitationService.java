package site.praytogether.pray_together.domain.friend.domain.friend_invitation;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.friend.domain.exception.FriendInvitationNotFoundException;
import site.praytogether.pray_together.domain.member.model.Member;

@Component
@RequiredArgsConstructor
public class FriendInvitationService {
  private final FriendInvitationRepository friendInvitationRepository;

  public void invite(Member inviter, Member invitee) {
    FriendInvitation friendInvitation = FriendInvitation.create(inviter, invitee);
    friendInvitationRepository.save(friendInvitation);
  }

  public List<FriendInvitation> getReceivedPendingInvitations(Long memberId) {
    return friendInvitationRepository.findAllByReceiver_IdAndStatus(memberId,FriendInvitationStatus.PENDING);
  }

  public FriendInvitation getReceivedInvitation(Long receiverId, Long invitationId) {
    return friendInvitationRepository.findByReceiver_IdAndId(receiverId,invitationId)
        .orElseThrow(() -> new FriendInvitationNotFoundException(receiverId,invitationId));
  }

  public FriendInvitation respondToInvitation(Long receiverId, Long invitationId, FriendInvitationStatus newStatus) {
    // 1. 받은 초대 상태 업데이트
    FriendInvitation receivedInvitation = getReceivedInvitation(receiverId, invitationId);
    receivedInvitation.updateStatus(newStatus);

    // 2. 양방향 초대 처리 (도메인 규칙)
    syncBidirectionalInvitation(receivedInvitation, newStatus);

    return receivedInvitation;
  }

  private void syncBidirectionalInvitation(FriendInvitation receivedInvitation, FriendInvitationStatus newStatus) {
    // 상대방이 나에게 보낸 초대가 있는지 확인
    Long senderId = receivedInvitation.getSender().getId();
    Long receiverId = receivedInvitation.getReceiver().getId();

    // 역방향 초대 찾기 (B->A 초대가 PENDING 상태인 경우)
    Optional<FriendInvitation> reverseInvitation = friendInvitationRepository
        .findBySender_IdAndReceiver_IdAndStatus(receiverId, senderId, FriendInvitationStatus.PENDING);

    // 역방향 초대도 같은 상태로 업데이트
    reverseInvitation.ifPresent(invitation -> invitation.updateStatus(newStatus));
  }
}
