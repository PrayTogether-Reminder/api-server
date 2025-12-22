package site.praytogether.pray_together.test_config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.invitation.domain.Invitation;
import site.praytogether.pray_together.domain.invitation.domain.repository.InvitationRepository;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.room.model.Room;

/**
 * 방 초대 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestInvitationUtils {

  private final InvitationRepository invitationRepository;

  public Invitation createSave(Member inviter, Member invitee, Room room) {
    Invitation invitation = Invitation.create(inviter, invitee, room);
    return invitationRepository.save(invitation);
  }
}
