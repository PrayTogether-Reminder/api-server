package site.praytogether.pray_together.domain.invitation.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
import site.praytogether.pray_together.domain.invitation.presentation.dto.InvitationCreateRequest;
import site.praytogether.pray_together.domain.invitation.presentation.dto.InvitationInfoScrollResponse;
import site.praytogether.pray_together.domain.invitation.presentation.dto.InvitationStatusUpdateRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invitations")
public class InvitationController {
  private final InvitationApplicationService invitationApplication;

  @PostMapping
  public ResponseEntity<MessageResponse> inviteMemberToRoom(
      @PrincipalId Long memberId, @Valid @RequestBody InvitationCreateRequest request) {
    MessageResponse response = invitationApplication.inviteMemberToRoom(memberId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<InvitationInfoScrollResponse> getPendingScrollInvitations(
      @PrincipalId Long memberId) {
    InvitationInfoScrollResponse response = invitationApplication.getInvitationInfoScroll(memberId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/{invitationId}")
  public ResponseEntity<MessageResponse> updateInvitationStatus(
      @PrincipalId Long memberId,
      @Positive(message = "잘 못된 초대장 입니다.") @PathVariable Long invitationId,
      @RequestBody InvitationStatusUpdateRequest request) {
    MessageResponse response =
        invitationApplication.updateInvitationStatus(memberId, invitationId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
