package site.praytogether.pray_together.domain.auth.application;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.auth.dto.AuthTokenReissueRequest;
import site.praytogether.pray_together.domain.auth.dto.AuthTokenReissueResponse;
import site.praytogether.pray_together.domain.auth.dto.OtpVerifyRequest;
import site.praytogether.pray_together.domain.auth.dto.SignupRequest;
import site.praytogether.pray_together.domain.auth.exception.RefreshTokenNotValidException;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.auth.service.OtpService;
import site.praytogether.pray_together.domain.auth.service.RefreshTokenService;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.security.service.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationService {
  private final MemberService memberService;
  private final OtpService otpService;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public void signup(SignupRequest request) {
    memberService.validateMemberNotExists(request.getEmail());
    memberService.createMember(request.getName(), request.getEmail(), request.getPassword());
  }

  public void sendOtp(String email) {
    memberService.validateMemberNotExists(email);
    otpService.sendOtp(email);
  }

  public boolean verifyOtp(OtpVerifyRequest request) {
    return otpService.verifyOtp(request.getEmail(), request.getOtp());
  }

  public AuthTokenReissueResponse reissueAuthToken(
      PrayTogetherPrincipal principal, AuthTokenReissueRequest request) {

    String memberId = String.valueOf(principal.getId());
    refreshTokenService.validateRefreshTokenExist(memberId, request.getRefreshToken());

    try {
      jwtService.isValid(request.getRefreshToken());
    } catch (JwtException e) {
      throw new RefreshTokenNotValidException(Long.valueOf(memberId));
    }

    refreshTokenService.delete(memberId);
    String access = jwtService.issueAccessToken(principal);
    String refresh = jwtService.issueRefreshToken(principal);
    refreshTokenService.save(memberId, refresh);

    return AuthTokenReissueResponse.of(access, refresh);
  }
}
