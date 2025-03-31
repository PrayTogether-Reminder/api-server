package site.praytogether.pray_together.domain.memberroom.service;

import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_AFTER;
import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.ROOMS_INFINITE_SCROLL_SIZE;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.memberroom.model.RoomIdMemberCnt;
import site.praytogether.pray_together.domain.memberroom.model.RoomInfo;
import site.praytogether.pray_together.domain.memberroom.repository.MemberRoomRepository;
import site.praytogether.pray_together.domain.room.dto.RoomScrollRequest;

@Service
@RequiredArgsConstructor
public class MemberRoomService {
  private final MemberRoomRepository memberRoomRepository;

  public LinkedHashMap<Long, RoomInfo> fetchRoomsByMember(
      Long memberId, RoomScrollRequest request) {
    // Todo: request의 orderBy 및 dir에 따른 repository 메서드 차별과 필요 - 전략패턴 (time,name,memberCnt ...) ?

    List<RoomInfo> roomInfos;
    if (DEFAULT_INFINITE_SCROLL_AFTER.equals(request.getAfter())) {
      roomInfos = fetchRoomsByFirst(memberId);
    } else {
      roomInfos = fetchRoomsByAfter(memberId, request);
    }
    return convertToLinkedMap(roomInfos);
  }

  private List<RoomInfo> fetchRoomsByAfter(Long memberId, RoomScrollRequest request) {
    return memberRoomRepository.findRoomInfoOrderByJoinedTimeDesc(
        memberId, Instant.parse(request.getAfter()), PageRequest.of(0, ROOMS_INFINITE_SCROLL_SIZE));
  }

  private List<RoomInfo> fetchRoomsByFirst(Long memberId) {
    return memberRoomRepository.findFirstRoomInfoOrderByJoinedTimeDesc(
        memberId, PageRequest.of(0, ROOMS_INFINITE_SCROLL_SIZE));
  }

  private LinkedHashMap<Long, RoomInfo> convertToLinkedMap(List<RoomInfo> roomInfos) {
    return roomInfos.stream()
        .collect(
            Collectors.toMap(
                RoomInfo::getRoomId,
                info -> info,
                (existing, replacement) -> existing,
                LinkedHashMap::new));
  }

  public List<RoomIdMemberCnt> fetchRoomIdAndMemberCnt(List<Long> roomIds) {
    return memberRoomRepository.findMemberCntByIds(roomIds);
  }
}
