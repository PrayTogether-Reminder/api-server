package site.praytogether.pray_together.domain.invitation.presentation.v2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.invitation.application.InvitationApplicationService;
import site.praytogether.pray_together.domain.invitation.presentation.v2.dto.InvitationCreateRequestV2;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/invitations")
public class InvitationControllerV2 {
  private final InvitationApplicationService invitationApplication;

  @PostMapping
  public ResponseEntity<MessageResponse> inviteMemberToRoom(
      @PrincipalId Long memberId, @Valid @RequestBody InvitationCreateRequestV2 request) {
    log.info(
        "[API] 기도방 초대(v2) 시작 memberId={} roomId={}",
        memberId, request.getRoomId());
    MessageResponse response = invitationApplication.inviteMemberToRoom(memberId, request);
    log.info(
        "[API] 기도방 초대(v2) 종료 memberId={} roomId={}",
        memberId, request.getRoomId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
