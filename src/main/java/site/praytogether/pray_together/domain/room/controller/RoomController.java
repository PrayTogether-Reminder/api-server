package site.praytogether.pray_together.domain.room.controller;

import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_AFTER;
import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_DIR;
import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.DEFAULT_INFINITE_SCROLL_ORDER_BY;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.room.applicatoin.RoomApplicationService;
import site.praytogether.pray_together.domain.room.dto.RoomCreateRequest;
import site.praytogether.pray_together.domain.room.dto.RoomInfiniteScrollRequest;
import site.praytogether.pray_together.domain.room.dto.RoomInfiniteScrollResponse;
import site.praytogether.pray_together.domain.room.dto.RoomMemberResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/v1/rooms")
public class RoomController {

  private final RoomApplicationService roomApplication;

  @GetMapping
  public ResponseEntity<RoomInfiniteScrollResponse> getRoomsByInfiniteScroll(
      @PrincipalId Long memberId,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_ORDER_BY) String orderBy,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_AFTER) String after,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_DIR) String dir) {
    RoomInfiniteScrollRequest request = RoomInfiniteScrollRequest.of(orderBy, after, dir);
    RoomInfiniteScrollResponse roomInfiniteScrollResponse =
        roomApplication.fetchRoomsInfiniteScroll(memberId, request);
    return ResponseEntity.status(HttpStatus.OK).body(roomInfiniteScrollResponse);
  }

  @PostMapping
  public ResponseEntity<MessageResponse> createRoom(
      @PrincipalId Long memberId, @Valid @RequestBody RoomCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(roomApplication.createRoom(memberId, request));
  }

  @DeleteMapping("/{roomId}")
  public ResponseEntity<MessageResponse> deleteRoom(
      @PrincipalId Long memberId,
      @Positive(message = "잘 못된 방을 선택하셨습니다.") @PathVariable Long roomId) {
    return ResponseEntity.status(HttpStatus.OK).body(roomApplication.deleteRoom(memberId, roomId));
  }

  @GetMapping("/{roomId}/members")
  public ResponseEntity<RoomMemberResponse> getRoomMembers(
      @PrincipalId Long memberId,
      @Min(value = 1, message = "잘 못된 방을 선택하셨습니다.") @PathVariable Long roomId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(roomApplication.listRoomMembers(memberId, roomId));
  }
}
