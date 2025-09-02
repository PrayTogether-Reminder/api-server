package site.praytogether.pray_together.domain.friend.domain.friend_invitation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.member.model.Member;

@Component
@RequiredArgsConstructor
public class FriendInvitationService {
  private final FriendInvitationRepository friendInvitationRepository;

  public void invite(Member inviter, Member invitee) {
    FriendInvitation friendInvitation = FriendInvitation.create(inviter, invitee);
    friendInvitationRepository.save(friendInvitation);
  }
}
