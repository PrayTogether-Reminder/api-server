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
    log.info("[API] 방 목록 조회 시작 memberId={} orderBy={} after={} dir={}", memberId, orderBy, after, dir);
    RoomInfiniteScrollRequest request = RoomInfiniteScrollRequest.of(orderBy, after, dir);
    RoomInfiniteScrollResponse roomInfiniteScrollResponse =
        roomApplication.fetchRoomsInfiniteScroll(memberId, request);
    log.info("[API] 방 목록 조회 종료 memberId={}", memberId);
    return ResponseEntity.status(HttpStatus.OK).body(roomInfiniteScrollResponse);
  }

  @PostMapping
  public ResponseEntity<MessageResponse> createRoom(
      @PrincipalId Long memberId, @Valid @RequestBody RoomCreateRequest request) {
    log.info("[API] 방 생성 시작 memberId={} roomName={}", memberId, request.getName());
    MessageResponse response = roomApplication.createRoom(memberId, request);
    log.info("[API] 방 생성 종료 memberId={} roomName={}", memberId, request.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }

  @DeleteMapping("/{roomId}")
  public ResponseEntity<MessageResponse> deleteRoom(
      @PrincipalId Long memberId,
      @Positive(message = "잘 못된 방을 선택하셨습니다.") @PathVariable Long roomId) {
    log.info("[API] 방 삭제 시작 memberId={} roomId={}", memberId, roomId);
    MessageResponse response = roomApplication.deleteRoom(memberId, roomId);
    log.info("[API] 방 삭제 종료 memberId={} roomId={}", memberId, roomId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{roomId}/members")
  public ResponseEntity<RoomMemberResponse> getRoomMembers(
      @PrincipalId Long memberId,
      @Min(value = 1, message = "잘 못된 방을 선택하셨습니다.") @PathVariable Long roomId) {
    log.info("[API] 방 멤버 조회 시작 memberId={} roomId={}", memberId, roomId);
    RoomMemberResponse response = roomApplication.listRoomMembers(memberId, roomId);
    log.info("[API] 방 멤버 조회 종료 memberId={} roomId={}", memberId, roomId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
