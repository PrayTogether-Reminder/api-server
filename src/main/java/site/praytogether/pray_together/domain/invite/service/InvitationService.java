package site.praytogether.pray_together.domain.invite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.invite.dto.InvitationCreateRequest;
import site.praytogether.pray_together.domain.invite.model.Invitation;
import site.praytogether.pray_together.domain.invite.repository.InvitationRepository;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.room.model.Room;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationService {
  private final InvitationRepository invitationRepository;

  @Transactional
  public Invitation create(Member member, Room room, InvitationCreateRequest request) {
    Invitation invitation = Invitation.create(member, room, request.getEmail());
    return invitationRepository.save(invitation);
  }
}
