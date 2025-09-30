package site.praytogether.pray_together.domain.friend.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.friend.application.mapper.FriendshipMapper;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationService;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationStatus;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;
import site.praytogether.pray_together.domain.friend.domain.friendship.FriendshipService;
import site.praytogether.pray_together.domain.friend.presentation.dto.FetchFriendListResponse;
import site.praytogether.pray_together.domain.friend.presentation.dto.FetchReceivedInvitationResponse;
import site.praytogether.pray_together.domain.friend.application.mapper.FriendInvitationMapper;
import site.praytogether.pray_together.domain.friend.presentation.dto.FriendshipDto;
import site.praytogether.pray_together.domain.friend.presentation.dto.UpdateReceivedInvitationRequest;
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
    // 자기 자신 초대 검증
    friendInvitationService.ensureNotSelfInvitation(inviterId, inviteeId);

    Member invitee = memberService.fetchById(inviteeId);
    Member inviter = memberService.fetchById(inviterId);

    // 이미 친구인지 검증
    friendshipService.ensureAlreadyNotFriends(inviter, invitee);

    // 중복 초대 검증
    friendInvitationService.ensureNoDuplicateInvitation(inviter, invitee);

    friendInvitationService.invite(inviter, invitee);
    return MessageResponse.of("친구 초대를 완료했습니다.");
  }

  public FetchReceivedInvitationResponse getReceivedPendingInvitations(Long receiverId) {
    Member receiver = memberService.fetchById(receiverId);
    List<FriendInvitation> receivedInvitations = friendInvitationService.getReceivedPendingInvitations(receiver.getId());
    return FriendInvitationMapper.toFetchReceivedInvitationResponse(receivedInvitations);
  }

  public MessageResponse updateReceivedInvitation(Long receiverId, Long invitationId, UpdateReceivedInvitationRequest request) {
    FriendInvitation invitation = friendInvitationService.respondToInvitation(receiverId, invitationId, request.status());

    if (request.status() == FriendInvitationStatus.ACCEPTED) {
      friendshipService.createFriendshipIfNotExists(invitation.getSender(), invitation.getReceiver());
    }

    String message = switch (request.status()) {
        case ACCEPTED -> "친구 요청을 수락했습니다.";
        case REJECTED -> "친구 요청을 거절했습니다.";
        default -> "친구 요청을 처리했습니다.";
    };

    return MessageResponse.of(message);
  }

  public FetchFriendListResponse getFriendList(Long memberId) {
    Member member = memberService.fetchById(memberId);
    List<Friendship> friendships = friendshipService.fetchListBy(member);
    List<FriendshipDto> dtos = FriendshipMapper.toDtos(friendships, member);
    return new FetchFriendListResponse(dtos);
  }

  public MessageResponse deleteFriend(Long memberId, Long friendId) {
    // 자기 자신 삭제 검증
    friendshipService.ensureNotSameMember(memberId, friendId);

    Member member = memberService.fetchById(memberId);
    Member friend = memberService.fetchById(friendId);

    friendshipService.delete(member, friend);

    return MessageResponse.of("친구 관계를 삭제했습니다.");
  }
}
