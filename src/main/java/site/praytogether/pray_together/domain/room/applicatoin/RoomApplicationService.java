package site.praytogether.pray_together.domain.room.applicatoin;

import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.MemberIdName;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.domain.member_room.exception.MemberRoomNotFoundException;
import site.praytogether.pray_together.domain.member_room.model.RoomIdMemberCount;
import site.praytogether.pray_together.domain.member_room.model.RoomInfo;
import site.praytogether.pray_together.domain.member_room.service.MemberRoomService;
import site.praytogether.pray_together.domain.room.dto.RoomCreateRequest;
import site.praytogether.pray_together.domain.room.dto.RoomMemberResponse;
import site.praytogether.pray_together.domain.room.dto.RoomScrollRequest;
import site.praytogether.pray_together.domain.room.dto.RoomScrollResponse;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;
import site.praytogether.pray_together.domain.room.service.RoomService;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomApplicationService {

  private final RoomService roomService;
  private final MemberService memberService;
  private final MemberRoomService memberRoomService;

  @Transactional
  public MessageResponse createRoom(Long memberId, RoomCreateRequest createRequest) {
    Room createdRoom =
        roomService.createRoom(createRequest.getName(), createRequest.getDescription());
    Member memberRef = memberService.getRefOrThrow(memberId);
    memberRoomService.addMemberToRoom(memberRef, createdRoom, RoomRole.OWNER);
    return MessageResponse.of("방 생성을 완료했습니다.");
  }

  public RoomScrollResponse fetchRoomsInfiniteScroll(
      Long memberId, RoomScrollRequest scrollRequest) {
    LinkedHashMap<Long, RoomInfo> roomInfoMap =
        memberRoomService.fetchRoomsByMember(memberId, scrollRequest);

    List<Long> roomIds = roomInfoMap.keySet().stream().toList();
    List<RoomIdMemberCount> roomMemberCounts = memberRoomService.fetchRoomMemberCounts(roomIds);

    roomMemberCounts.forEach(
        roomMemberCount ->
            roomInfoMap
                .get(roomMemberCount.getRoomId())
                .setMemberCnt(roomMemberCount.getMemberCnt()));

    return RoomScrollResponse.from(roomInfoMap);
  }

  @Transactional
  public MessageResponse deleteRoom(Long memberId, Long roomId) {
    memberService.validateMemberExists(memberId);
    boolean deleteResult = memberRoomService.deleteMemberRoomById(memberId, roomId);
    if (deleteResult == false) {
      throw new MemberRoomNotFoundException(memberId, roomId);
    }
    return MessageResponse.of("방을 나갔습니다.");
  }

  public RoomMemberResponse listRoomParticipants(Long memberId, Long roomId) {
    memberRoomService.validateMemberExistInRoom(memberId, roomId);
    List<MemberIdName> memberIdNames = memberRoomService.fetchMembersInRoom(roomId);
    return RoomMemberResponse.from(memberIdNames);
  }
}
