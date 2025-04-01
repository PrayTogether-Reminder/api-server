package site.praytogether.pray_together.domain.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.auth.dto.OtpVerifyRequest;
import site.praytogether.pray_together.domain.auth.service.AuthService;
import site.praytogether.pray_together.domain.member.service.MemberService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationService {
  private final MemberService memberService;
  private final AuthService authService;

  public void sendOtp(String email) {
    memberService.validateMemberNotExists(email);
    authService.sendOtp(email);
  }

  public boolean verifyOtp(OtpVerifyRequest request) {
    return authService.verifyOtp(request.getEmail(), request.getOtp());
  }
}
