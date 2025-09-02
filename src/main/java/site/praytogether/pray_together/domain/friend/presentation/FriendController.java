package site.praytogether.pray_together.domain.friend.presentation;

import jakarta.mail.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.friend.application.FriendApplicationService;

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
}
