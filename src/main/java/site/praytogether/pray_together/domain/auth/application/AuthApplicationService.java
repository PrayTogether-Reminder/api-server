package site.praytogether.pray_together.domain.auth.application;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.auth.presentation.dto.AuthTokenReissueRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.AuthTokenReissueResponse;
import site.praytogether.pray_together.domain.auth.presentation.dto.OtpVerifyRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.SignupRequest;
import site.praytogether.pray_together.domain.auth.domain.exception.RefreshTokenNotValidException;
import site.praytogether.pray_together.domain.auth.domain.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.auth.domain.SignupCommand;
import site.praytogether.pray_together.domain.auth.domain.OtpService;
import site.praytogether.pray_together.domain.auth.domain.RefreshTokenService;
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
      refreshTokenService.validateRefreshTokenExist(memberId, refreshToken);

      jwtService.isValid(request.getRefreshToken());
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
