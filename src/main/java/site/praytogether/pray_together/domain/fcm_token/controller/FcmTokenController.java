package site.praytogether.pray_together.domain.fcm_token.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.fcm_token.application.FcmTokenApplicationService;
import site.praytogether.pray_together.domain.fcm_token.dto.FcmTokenDeleteRequest;
import site.praytogether.pray_together.domain.fcm_token.dto.FcmTokenRegisterRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm-token")
public class FcmTokenController {
  private final FcmTokenApplicationService fcmTokenApplication;

  @PostMapping
  public ResponseEntity<Void> registerFcmToken(
      @PrincipalId Long memberId, @Valid @RequestBody FcmTokenRegisterRequest request) {
    fcmTokenApplication.registerFcmToken(request, memberId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteFcmToken(@PrincipalId Long memberId, @Valid @RequestBody FcmTokenDeleteRequest request) {
    fcmTokenApplication.deleteFcmToken(request, memberId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
