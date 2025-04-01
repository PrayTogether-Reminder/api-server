package site.praytogether.pray_together.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.application.AuthApplicationService;
import site.praytogether.pray_together.domain.auth.dto.EmailOtpRequest;
import site.praytogether.pray_together.domain.auth.dto.OtpVerifyRequest;
import site.praytogether.pray_together.domain.base.MessageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
  private final AuthApplicationService authApplication;

  //  @PostMapping("/signup")
  //    public ResponseEntity<MessageResponse> signup()

  @PostMapping("/otp/email")
  public ResponseEntity<MessageResponse> getEmailOtp(@Valid @RequestBody EmailOtpRequest request) {
    log.info("[API] OTP 요청 시작{}", request.getEmail());
    authApplication.sendOtp(request.getEmail());
    log.info("[API] OTP 요청 종료{}", request.getEmail());
    return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of("인증 번호를 요청했습니다."));
  }

  @PostMapping("/otp/email/verification")
  public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
    log.info("[API] OTP 검증 요청 시작{}:{}", request.getEmail(), request.getOtp());
    boolean otpResult = authApplication.verifyOtp(request);
    log.info("[API] OTP 검증 요청 종료{}:{}", request.getEmail(), request.getOtp());
    if (otpResult == false) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(MessageResponse.of("인증 번호가 일치하지 않습니다."));
    }
    return ResponseEntity.status(HttpStatus.OK).body(MessageResponse.of("인증에 성공했습니다."));
  }
}
