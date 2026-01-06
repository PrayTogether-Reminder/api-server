package site.praytogether.pray_together.domain.auth.application;

import io.jsonwebtoken.JwtException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.auth.domain.AuthService;
import site.praytogether.pray_together.domain.auth.domain.OtpService;
import site.praytogether.pray_together.domain.auth.domain.PasswordReissuer;
import site.praytogether.pray_together.domain.auth.domain.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.auth.domain.RefreshTokenService;
import site.praytogether.pray_together.domain.auth.domain.SignupCommand;
import site.praytogether.pray_together.domain.auth.domain.event.PasswordReissuedEvent;
import site.praytogether.pray_together.domain.auth.domain.exception.RefreshTokenNotValidException;
import site.praytogether.pray_together.domain.auth.infrastructure.AppleTokenVerifier;
import site.praytogether.pray_together.domain.auth.infrastructure.GoogleTokenVerifier;
import site.praytogether.pray_together.domain.auth.presentation.dto.AppleAuthRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.AppleAuthResponse;
import site.praytogether.pray_together.domain.auth.presentation.dto.AuthTokenReissueRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.AuthTokenReissueResponse;
import site.praytogether.pray_together.domain.auth.presentation.dto.ChangePasswordRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.GoogleAuthRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.GoogleAuthResponse;
import site.praytogether.pray_together.domain.auth.presentation.dto.GoogleSignupRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.LoginResponse;
import site.praytogether.pray_together.domain.auth.presentation.dto.OtpVerifyRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.ReissuePasswordRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.SignupRequest;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.expcetion.MemberAlreadyExistException;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.PhoneNumber;
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
  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;
  private final PasswordReissuer passwordReissuer;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;
  private final GoogleTokenVerifier googleTokenVerifier;
  private final AppleTokenVerifier appleTokenVerifier;

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

  public MessageResponse reissuePassword(ReissuePasswordRequest request) {
    Member member = memberService.fetchByEmail(request.getEmail());
    authService.validateLocalAuthentication(member);
    String newPw = passwordReissuer.generatedByRandom();
    String encodePw = passwordEncoder.encode(newPw);
    member.updatePassword(encodePw);

    eventPublisher.publishEvent(PasswordReissuedEvent.of(member.getEmail(), newPw));

    return MessageResponse.of("임시 비밀번호를 이메일로 전송했습니다.");
  }

  public MessageResponse changePassword(Long memberId, ChangePasswordRequest request) {
    Member member = memberService.fetchById(memberId);
    authService.validateLocalAuthentication(member);
    String encodedPassword = passwordEncoder.encode(request.getNewPassword());
    member.updatePassword(encodedPassword);

    return MessageResponse.of("비밀번호를 변경했습니다.");
  }

  public GoogleAuthResponse googleAuth(GoogleAuthRequest request) {
    googleTokenVerifier.verify(request.getIdToken());

    Optional<Member> memberOptional = memberService.findByEmail(request.getEmail());

    if (memberOptional.isEmpty()) {
      return GoogleAuthResponse.newMember();
    }

    Member member = memberOptional.get();
    PrayTogetherPrincipal principal = PrayTogetherPrincipal.builder()
        .id(member.getId())
        .email(member.getEmail())
        .build();

    String accessToken = jwtService.issueAccessToken(principal);
    String refreshToken = jwtService.issueRefreshToken(principal);
    refreshTokenService.save(member, refreshToken, jwtService.extractExpiration(refreshToken));

    return GoogleAuthResponse.existingMember(accessToken, refreshToken);
  }

  public LoginResponse googleSignup(GoogleSignupRequest request) {
    googleTokenVerifier.verify(request.getIdToken());

    if (memberService.isExistMember(request.getEmail())) {
      throw new MemberAlreadyExistException(request.getEmail());
    }

    PhoneNumber phoneNumber = PhoneNumber.of(request.getPhoneNumber());
    Member member = memberService.createGoogleMember(request.getName(), request.getEmail(), phoneNumber);

    PrayTogetherPrincipal principal = PrayTogetherPrincipal.builder()
        .id(member.getId())
        .email(member.getEmail())
        .build();

    String accessToken = jwtService.issueAccessToken(principal);
    String refreshToken = jwtService.issueRefreshToken(principal);
    refreshTokenService.save(member, refreshToken, jwtService.extractExpiration(refreshToken));

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public AppleAuthResponse appleAuth(AppleAuthRequest request) {
    String appleUserId = appleTokenVerifier.verify(request.getIdentityToken());

    Optional<Member> memberOptional = memberService.findByProviderMemberId(appleUserId);

    Member member;
    boolean needsPhoneNumber;

    if (memberOptional.isEmpty()) {
      // 신규 회원 - 바로 생성 (전화번호 없이)
      member = memberService.createAppleMember(request.getName(), appleUserId);
      needsPhoneNumber = true;
    } else {
      member = memberOptional.get();
      needsPhoneNumber = member.getPhoneNumber() == null;
    }

    PrayTogetherPrincipal principal = PrayTogetherPrincipal.builder()
        .id(member.getId())
        .email(member.getEmail())
        .build();

    String accessToken = jwtService.issueAccessToken(principal);
    String refreshToken = jwtService.issueRefreshToken(principal);
    refreshTokenService.save(member, refreshToken, jwtService.extractExpiration(refreshToken));

    return AppleAuthResponse.of(accessToken, refreshToken, needsPhoneNumber);
  }
}
