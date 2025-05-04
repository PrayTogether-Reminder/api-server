package site.praytogether.pray_together.domain.invitation.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.invitation.dto.InvitationCreateRequest;
import site.praytogether.pray_together.domain.invitation.dto.InvitationInfoScrollResponse;
import site.praytogether.pray_together.domain.invitation.dto.InvitationStatusUpdateRequest;
import site.praytogether.pray_together.domain.invitation.model.Invitation;
import site.praytogether.pray_together.domain.invitation.model.InvitationInfo;
import site.praytogether.pray_together.domain.invitation.service.InvitationService;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.domain.member_room.service.MemberRoomService;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;
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
        invitationService.fetchInvitationScrollByMemberId(memberId);
    return InvitationInfoScrollResponse.from(invitationInfos);
  }

  @Transactional
  public MessageResponse updateInvitationStatus(
      Long memberId, Long invitationId, InvitationStatusUpdateRequest request) {
    Invitation invitation = invitationService.fetchByInviteeIdAndId(memberId, invitationId);
    switch (request.getStatus()) {
      case ACCEPTED -> {
        invitationService.accept(invitation);
        Member invitee = invitation.getInvitee();
        Room room = invitation.getRoom();
        memberRoomService.addMemberToRoom(invitee, room, RoomRole.MEMBER);
      }
      case REJECTED -> invitationService.reject(invitation);
    }
    return MessageResponse.of(
        String.format("기도방 초대를 %s했습니다.", request.getStatus().getKoreanName()));
  }

  @Transactional
  public MessageResponse inviteMemberToRoom(Long inviterMemberId, InvitationCreateRequest request) {
    memberRoomService.validateMemberExistInRoom(inviterMemberId, request.getRoomId());
    Member inviter = memberService.fetchById(inviterMemberId);
    Member invitee = memberService.getByEmail(request.getEmail());
    memberRoomService.validateMemberNotExistInRoom(invitee.getId(), request.getRoomId());
    Room roomRef = roomService.getRefOrThrow(request.getRoomId());
    invitationService.create(inviter, invitee, roomRef);
    return MessageResponse.of("초대를 완료했습니다.");
  }
}
