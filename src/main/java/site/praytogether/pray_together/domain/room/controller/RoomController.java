package site.praytogether.pray_together.domain.room.controller;

import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_AFTER;
import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_DIR;
import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_ORDER_BY;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrayTogetherMemberId;
import site.praytogether.pray_together.domain.room.dto.RoomScrollRequest;
import site.praytogether.pray_together.domain.room.dto.RoomScrollResponse;
import site.praytogether.pray_together.domain.room.service.RoomService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/rooms")
public class RoomController {

  private final RoomService roomService;

  @GetMapping
  public ResponseEntity<RoomScrollResponse> getRoomsByScroll(
      @PrayTogetherMemberId Long memberId,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_ORDER_BY) String orderBy,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_AFTER) String after,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_DIR) String dir) {
    RoomScrollRequest request = RoomScrollRequest.of(orderBy, after, dir);
    RoomScrollResponse roomScrollResponse = roomService.fetchRoomsInfiniteScroll(memberId, request);
    return ResponseEntity.status(HttpStatus.OK).body(roomScrollResponse);
  }
}
