package site.praytogether.pray_together.domain.invite.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.invite.dto.InvitationCreateRequest;
import site.praytogether.pray_together.domain.invite.service.InvitationService;
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

  @Transactional
  public MessageResponse inviteMemberToRoom(Long memberId, InvitationCreateRequest request) {
    Member memberRef = memberService.getRefOrThrow(memberId);
    Room roomRef = roomService.getRefOrThrow(request.getRoomId());
    memberRoomService.validateMemberExistInRoom(memberRef.getId(), roomRef.getId());
    invitationService.create(memberRef, roomRef, request);
    return MessageResponse.of("초대를 완료했습니다.");
  }
}
