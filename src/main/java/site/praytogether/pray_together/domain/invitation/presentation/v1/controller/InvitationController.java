package site.praytogether.pray_together.domain.invitation.presentation.v1.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.invitation.application.InvitationApplicationService;
import site.praytogether.pray_together.domain.invitation.presentation.v1.dto.InvitationCreateRequest;
import site.praytogether.pray_together.domain.invitation.presentation.v1.dto.InvitationInfoScrollResponse;
import site.praytogether.pray_together.domain.invitation.presentation.v1.dto.InvitationStatusUpdateRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invitations")
public class InvitationController {
  private final InvitationApplicationService invitationApplication;

  @PostMapping
  public ResponseEntity<MessageResponse> inviteMemberToRoom(
      @PrincipalId Long memberId, @Valid @RequestBody InvitationCreateRequest request) {
    log.info(
        "[API] 기도방 초대 시작 memberId={} roomId={}", memberId, request.getRoomId());
    MessageResponse response = invitationApplication.inviteMemberToRoom(memberId, request);
    log.info(
        "[API] 기도방 초대 종료 memberId={} roomId={}", memberId, request.getRoomId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<InvitationInfoScrollResponse> getPendingScrollInvitations(
      @PrincipalId Long memberId) {
    log.info("[API] 기도방 초대 목록 조회 시작 memberId={}", memberId);
    InvitationInfoScrollResponse response = invitationApplication.getInvitationInfoScroll(memberId);
    log.info("[API] 기도방 초대 목록 조회 종료 memberId={}", memberId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/{invitationId}")
  public ResponseEntity<MessageResponse> updateInvitationStatus(
      @PrincipalId Long memberId,
      @Positive(message = "잘 못된 초대장 입니다.") @PathVariable Long invitationId,
      @RequestBody InvitationStatusUpdateRequest request) {
    log.info(
        "[API] 기도방 초대 응답 시작 memberId={} invitationId={} status={}",
        memberId, invitationId, request.getStatus());
    MessageResponse response =
        invitationApplication.updateInvitationStatus(memberId, invitationId, request);
    log.info(
        "[API] 기도방 초대 응답 종료 memberId={} invitationId={} status={}",
        memberId, invitationId, request.getStatus());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
