package site.praytogether.pray_together.domain.room.controller;

import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_AFTER;
import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_DIR;
import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_ORDER_BY;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrayTogetherMemberId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.room.applicatoin.RoomApplicationService;
import site.praytogether.pray_together.domain.room.dto.RoomCreateRequest;
import site.praytogether.pray_together.domain.room.dto.RoomScrollRequest;
import site.praytogether.pray_together.domain.room.dto.RoomScrollResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/rooms")
public class RoomController {

  private final RoomApplicationService roomApplication;

  @GetMapping
  public ResponseEntity<RoomScrollResponse> getRoomsByScroll(
      @PrayTogetherMemberId Long memberId,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_ORDER_BY) String orderBy,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_AFTER) String after,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_DIR) String dir) {
    RoomScrollRequest request = RoomScrollRequest.of(orderBy, after, dir);
    RoomScrollResponse roomScrollResponse =
        roomApplication.fetchRoomsInfiniteScroll(memberId, request);
    return ResponseEntity.status(HttpStatus.OK).body(roomScrollResponse);
  }

  @PostMapping
  public ResponseEntity<MessageResponse> createRoom(
      @PrayTogetherMemberId Long memberId, RoomCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(roomApplication.createRoom(memberId, request));
  }

  @DeleteMapping("/:roomId")
  public ResponseEntity<MessageResponse> deleteRoom(
      @PrayTogetherMemberId Long memberId, @PathVariable Long roomId) {
    return ResponseEntity.status(HttpStatus.OK).body(roomApplication.deleteRoom(memberId, roomId));
  }
}
