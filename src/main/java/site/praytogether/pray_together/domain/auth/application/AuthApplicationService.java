package site.praytogether.pray_together.domain.auth.application;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.auth.dto.AuthTokenReissueRequest;
import site.praytogether.pray_together.domain.auth.dto.AuthTokenReissueResponse;
import site.praytogether.pray_together.domain.auth.dto.OtpVerifyRequest;
import site.praytogether.pray_together.domain.auth.dto.SignupRequest;
import site.praytogether.pray_together.domain.auth.exception.RefreshTokenNotValidException;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.auth.model.SignupCommand;
import site.praytogether.pray_together.domain.auth.service.OtpService;
import site.praytogether.pray_together.domain.auth.service.RefreshTokenService;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.service.MemberService;
import site.praytogether.pray_together.security.service.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthApplicationService {
  private final MemberService memberService;
  private final OtpService otpService;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public void signup(SignupRequest request) {
    SignupCommand command = SignupCommand.from(request);
    memberService.validateMemberNotExists(command.getEmail());
    memberService.create(command);
  }

  public void withdraw(Long memberId) {
    memberService.deleteMember(memberId);
  }

  public void sendOtp(String email) {
    memberService.validateMemberNotExists(email);
    otpService.sendOtp(email);
  }

  public boolean verifyOtp(OtpVerifyRequest request) {
    return otpService.verifyOtp(request.getEmail(), request.getOtp());
  }

  public AuthTokenReissueResponse reissueAuthToken(AuthTokenReissueRequest request) {
    Long memberId = 0L;
    try {
      String refreshToken = request.getRefreshToken();
      memberId = jwtService.extractMemberId(refreshToken);
      jwtService.validateRefresh(request.getRefreshToken());
      refreshTokenService.validateRefreshTokenExist(memberId, refreshToken);

    } catch (JwtException e) {
      throw new RefreshTokenNotValidException(memberId);
    }

    refreshTokenService.delete(memberId);
    Member member = memberService.fetchById(memberId);
    PrayTogetherPrincipal principal =
        PrayTogetherPrincipal.builder().id(member.getId()).email(member.getEmail()).build();
    String access = jwtService.issueAccessToken(principal);
    String refresh = jwtService.issueRefreshToken(principal);
    refreshTokenService.save(member, refresh, jwtService.extractExpiration(refresh));

    return AuthTokenReissueResponse.of(access, refresh);
  }
}
