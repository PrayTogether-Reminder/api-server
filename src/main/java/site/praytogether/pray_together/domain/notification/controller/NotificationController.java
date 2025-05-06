package site.praytogether.pray_together.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.notification.application.NotificationApplicationService;
import site.praytogether.pray_together.domain.notification.dto.FcmTokenRegisterRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
  private final NotificationApplicationService notificationApplication;

  @PostMapping("fcm-token")
  public ResponseEntity<Void> registerFcmToken(
      @PrincipalId Long memberId, @RequestBody FcmTokenRegisterRequest request) {
    notificationApplication.registerFcmToken(request, memberId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
