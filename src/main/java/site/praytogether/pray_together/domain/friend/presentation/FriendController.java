package site.praytogether.pray_together.domain.friend.presentation;

import lombok.RequiredArgsConstructor;
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
import site.praytogether.pray_together.domain.friend.application.FriendApplicationService;
import site.praytogether.pray_together.domain.friend.presentation.dto.FetchReceivedInvitationResponse;
import site.praytogether.pray_together.domain.friend.presentation.dto.UpdateReceivedInvitationRequest;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
  private final FriendApplicationService friendApplication;

  @PostMapping("/{inviteeId}/requests")
  public ResponseEntity<MessageResponse> inviteFriend(@PrincipalId Long inviterId, @PathVariable Long inviteeId) {
    MessageResponse response = friendApplication.inviteFriend(inviterId, inviteeId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/requests")
  public ResponseEntity<FetchReceivedInvitationResponse> getReceivedInvitations(@PrincipalId Long receiverId) {
    FetchReceivedInvitationResponse response = friendApplication.getReceivedPendingInvitations(receiverId);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/requests/{invitationId}")
  public ResponseEntity<MessageResponse> updateReceivedInvitation(@PrincipalId Long inviteeId, @PathVariable Long  invitationId, @RequestBody UpdateReceivedInvitationRequest request) {
    MessageResponse response = friendApplication.updateReceivedInvitation(inviteeId,invitationId, request);
    return ResponseEntity.ok(response);
  }
}
