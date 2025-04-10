package site.praytogether.pray_together.domain.invitation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.invitation.model.Invitation;
import site.praytogether.pray_together.domain.invitation.model.InvitationInfo;
import site.praytogether.pray_together.domain.invitation.model.InvitationStatus;
import site.praytogether.pray_together.domain.invitation.repository.InvitationRepository;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.room.model.Room;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationService {
  private final InvitationRepository invitationRepository;

  public List<InvitationInfo> fetchInvitationInfoScrollByMemberId(Long memberId) {
    return invitationRepository.findInfosByMemberId(memberId, InvitationStatus.PENDING);
  }

  @Transactional
  public Invitation create(Member inviter, Member invitee, Room room) {
    Invitation invitation = Invitation.create(inviter, invitee, room);

    return invitationRepository.save(invitation);
  }
}
