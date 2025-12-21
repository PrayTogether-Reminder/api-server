package site.praytogether.pray_together.test_config;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.auth.domain.RefreshToken;
import site.praytogether.pray_together.domain.auth.domain.RefreshTokenRepository;
import site.praytogether.pray_together.domain.member.model.Member;

/**
 * 리프레시 토큰 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestRefreshTokenUtils {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshToken createSave(Member member, String token, Instant expiredAt) {
    RefreshToken refreshToken = RefreshToken.create(member, token, expiredAt);
    refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }
}
