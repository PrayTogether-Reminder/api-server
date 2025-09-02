package site.praytogether.pray_together.domain.friend.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationService;
import site.praytogether.pray_together.domain.friend.domain.friendship.FriendshipService;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FriendApplicationService {
  private final FriendshipService friendshipService;
  private final FriendInvitationService friendInvitationService;
  private final MemberService memberService;

  public MessageResponse inviteFriend(Long inviterId,Long inviteeId) {
    Member invitee = memberService.fetchById(inviteeId);
    Member inviter = memberService.fetchById(inviterId);
    friendshipService.ensureAlreadyNotFriends(inviter, invitee);
    friendInvitationService.invite(inviter, invitee);
    return MessageResponse.of("친구 초대를 완료했습니다.");
  }
}
