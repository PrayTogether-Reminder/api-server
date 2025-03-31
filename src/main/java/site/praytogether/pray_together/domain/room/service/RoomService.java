package site.praytogether.pray_together.domain.room.service;

import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.memberroom.model.RoomIdMemberCnt;
import site.praytogether.pray_together.domain.memberroom.model.RoomInfo;
import site.praytogether.pray_together.domain.memberroom.service.MemberRoomService;
import site.praytogether.pray_together.domain.room.dto.RoomScrollRequest;
import site.praytogether.pray_together.domain.room.dto.RoomScrollResponse;
import site.praytogether.pray_together.domain.room.repository.RoomRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;
  private final MemberRoomService memberRoomService;

  public RoomScrollResponse fetchRoomsInfiniteScroll(Long memberId, RoomScrollRequest request) {
    LinkedHashMap<Long, RoomInfo> roomInfos =
        memberRoomService.fetchRoomsByMember(memberId, request);

    List<Long> roomIds = roomInfos.keySet().stream().toList();
    List<RoomIdMemberCnt> roomIdMembers = memberRoomService.fetchRoomIdAndMemberCnt(roomIds);
    roomIdMembers.forEach(
        idMember -> roomInfos.get(idMember.getRoomId()).setMemberCnt(idMember.getMemberCnt()));

    return RoomScrollResponse.of(roomInfos);
  }
}
