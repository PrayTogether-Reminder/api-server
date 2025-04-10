package site.praytogether.pray_together.domain.invitation.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.invitation.dto.InvitationCreateRequest;
import site.praytogether.pray_together.domain.invitation.dto.InvitationInfoScrollResponse;
import site.praytogether.pray_together.domain.invitation.model.InvitationInfo;
import site.praytogether.pray_together.domain.invitation.service.InvitationService;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.domain.member_room.service.MemberRoomService;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.service.RoomService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationApplicationService {

  private final InvitationService invitationService;
  private final RoomService roomService;
  private final MemberService memberService;
  private final MemberRoomService memberRoomService;

  public InvitationInfoScrollResponse getInvitationInfoScroll(Long memberId) {
    List<InvitationInfo> invitationInfos =
        invitationService.fetchInvitationInfoScrollByMemberId(memberId);
    return InvitationInfoScrollResponse.from(invitationInfos);
  }

  @Transactional
  public MessageResponse inviteMemberToRoom(Long inviterMemberId, InvitationCreateRequest request) {
    memberRoomService.validateMemberExistInRoom(inviterMemberId, request.getRoomId());
    Member inviter = memberService.getById(inviterMemberId);
    Member invitee = memberService.getByEmail(request.getEmail());
    Room roomRef = roomService.getRefOrThrow(request.getRoomId());
    invitationService.create(inviter, invitee, roomRef);
    return MessageResponse.of("초대를 완료했습니다.");
  }
}
