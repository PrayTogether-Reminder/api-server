package site.praytogether.pray_together.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.auth.application.AuthApplicationService;
import site.praytogether.pray_together.domain.auth.dto.AuthTokenReissueRequest;
import site.praytogether.pray_together.domain.auth.dto.AuthTokenReissueResponse;
import site.praytogether.pray_together.domain.auth.dto.EmailOtpRequest;
import site.praytogether.pray_together.domain.auth.dto.OtpVerifyRequest;
import site.praytogether.pray_together.domain.auth.dto.SignupRequest;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.base.MessageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
  private final AuthApplicationService authApplication;

  @PostMapping("/signup")
  public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest request) {
    log.info("[API] 회원 가입 요청 시작 email={}", request.getEmail());
    authApplication.signup(request);
    log.info("[API] 회원 가입 요청 종료 email={}", request.getEmail());
    return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponse.of("회원 가입을 완료했습니다."));
  }

  @DeleteMapping("/withdraw")
  public ResponseEntity<MessageResponse> withdraw(@PrincipalId Long memberId) {
    log.info("[API] 회원 탈퇴 요청 시작 memberId={}", memberId);
    authApplication.withdraw(memberId);
    log.info("[API] 회원 탈퇴 요청 종료 memberId={}", memberId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(MessageResponse.of("회워 탈퇴를 완료했습니다.\n 함께 기도해 주셔 감사합니다."));
  }

  @PostMapping("/otp/email")
  public ResponseEntity<MessageResponse> getEmailOtp(@Valid @RequestBody EmailOtpRequest request) {
    log.info("[API] OTP 요청 시작 email={}", request.getEmail());
    authApplication.sendOtp(request.getEmail());
    log.info("[API] OTP 요청 종료 email={}", request.getEmail());
    return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of("인증 번호를 요청했습니다."));
  }

  @PostMapping("/otp/email/verification")
  public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
    log.info("[API] OTP 검증 요청 시작 email={} otp={}", request.getEmail(), request.getOtp());
    boolean otpResult = authApplication.verifyOtp(request);
    log.info("[API] OTP 검증 요청 종료 email={} otp={}", request.getEmail(), request.getOtp());
    if (otpResult == false) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(MessageResponse.of("인증 번호가 일치하지 않습니다."));
    }
    return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of("인증에 성공했습니다."));
  }

  @PostMapping("/reissue-token")
  public ResponseEntity<AuthTokenReissueResponse> reissueRefreshToken(
      @AuthenticationPrincipal PrayTogetherPrincipal principal,
      @Valid @RequestBody AuthTokenReissueRequest request) {
    log.info("[API] JWT 재발급 요청 시작 memberId={}", principal.getId());
    AuthTokenReissueResponse response = authApplication.reissueAuthToken(principal, request);
    log.info("[API] JWT 재발급 요청 종료 memberId={}", principal.getId());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
