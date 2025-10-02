package site.praytogether.pray_together.domain.invitation.domain.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.invitation.domain.exception.InvitationNotFoundException;
import site.praytogether.pray_together.domain.invitation.domain.Invitation;
import site.praytogether.pray_together.domain.invitation.domain.InvitationInfo;
import site.praytogether.pray_together.domain.invitation.domain.InvitationStatus;
import site.praytogether.pray_together.domain.invitation.domain.repository.InvitationRepository;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.room.model.Room;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationService {
  private final InvitationRepository invitationRepository;

  public List<InvitationInfo> fetchInvitationScrollByMemberId(Long memberId) {
    return invitationRepository.findInfosByMemberId(memberId, InvitationStatus.PENDING);
  }

  public Invitation fetchByInviteeIdAndId(Long inviteeId, Long invitationId) {
    return invitationRepository
        .findByInvitee_IdAndId(inviteeId, invitationId)
        .orElseThrow(() -> new InvitationNotFoundException(inviteeId, invitationId));
  }

  @Transactional
  public Invitation create(Member inviter, Member invitee, Room room) {
    Invitation invitation = Invitation.create(inviter, invitee, room);

    return invitationRepository.save(invitation);
  }

  @Transactional
  public void accept(Invitation invitation) {
    invitation.accept();
  }

  @Transactional
  public void reject(Invitation invitation) {
    invitation.reject();
  }
}
