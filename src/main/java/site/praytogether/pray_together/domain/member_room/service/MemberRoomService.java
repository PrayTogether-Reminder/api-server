package site.praytogether.pray_together.domain.member_room.service;

import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_AFTER;
import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.ROOMS_INFINITE_SCROLL_SIZE;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.member_room.model.RoomIdMemberCnt;
import site.praytogether.pray_together.domain.member_room.model.RoomInfo;
import site.praytogether.pray_together.domain.member_room.repository.MemberRoomRepository;
import site.praytogether.pray_together.domain.room.dto.RoomScrollRequest;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRoomService {
  private final MemberRoomRepository memberRoomRepository;

  @Transactional
  public MemberRoom addMemberToRoom(Member member, Room room, RoomRole role) {
    MemberRoom memberRoom =
        MemberRoom.builder().member(member).room(room).role(role).isNotification(true).build();
    return memberRoomRepository.save(memberRoom);
  }

  public LinkedHashMap<Long, RoomInfo> fetchRoomsByMember(
      Long memberId, RoomScrollRequest scrollRequest) {
    // TODO: 전략 패턴으로 orderBy 및 dir에 따른 repository 메서드 차별화 구현 (time, name, memberCnt 등)

    List<RoomInfo> roomInfos;
    if (DEFAULT_INFINITE_SCROLL_AFTER.equals(scrollRequest.getAfter())) {
      roomInfos = fetchInitialRooms(memberId);
      return convertToOrderedMap(roomInfos);
    }

    roomInfos = fetchRoomsByAfter(memberId, scrollRequest);
    return convertToOrderedMap(roomInfos);
  }

  public List<RoomIdMemberCnt> fetchRoomMemberCounts(List<Long> roomIds) {
    return memberRoomRepository.findMemberCntByIds(roomIds);
  }

  private List<RoomInfo> fetchRoomsByAfter(Long memberId, RoomScrollRequest scrollRequest) {
    return memberRoomRepository.findRoomInfoOrderByJoinedTimeDesc(
        memberId,
        Instant.parse(scrollRequest.getAfter()),
        PageRequest.of(0, ROOMS_INFINITE_SCROLL_SIZE));
  }

  private List<RoomInfo> fetchInitialRooms(Long memberId) {
    return memberRoomRepository.findFirstRoomInfoOrderByJoinedTimeDesc(
        memberId, PageRequest.of(0, ROOMS_INFINITE_SCROLL_SIZE));
  }

  private LinkedHashMap<Long, RoomInfo> convertToOrderedMap(List<RoomInfo> roomInfos) {
    return roomInfos.stream()
        .collect(
            Collectors.toMap(
                RoomInfo::getRoomId,
                roomInfo -> roomInfo,
                (existing, replacement) -> existing,
                LinkedHashMap::new));
  }

  public boolean deleteMemberRoomById(Long memberId, Long roomId) {
    return memberRoomRepository.deleteByMember_IdAndRoom_Id(memberId, roomId);
  }
}
