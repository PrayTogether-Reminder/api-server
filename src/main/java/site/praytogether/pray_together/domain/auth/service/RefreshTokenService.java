package site.praytogether.pray_together.domain.auth.service;

import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.auth.exception.RefreshTokenNotFoundException;
import site.praytogether.pray_together.domain.auth.model.RefreshToken;
import site.praytogether.pray_together.domain.auth.repository.RefreshTokenRepository;
import site.praytogether.pray_together.domain.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final MemberService memberService;

  public void save(Long memberId, String token, Instant expiredTime) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByMemberId(memberId)
            .orElseGet(
                () ->
                    RefreshToken.create(
                        memberService.getRefOrThrow(memberId), token, expiredTime));

    if (refreshToken.getId() == null) {
      refreshTokenRepository.save(refreshToken);
      return;
    }

    refreshToken.updateToken(token, expiredTime);
  }

  public RefreshToken get(Long memberId) {
    return refreshTokenRepository
        .findByMemberId(memberId)
        .orElseThrow(() -> new RefreshTokenNotFoundException(memberId));
  }

  public void delete(Long memberId) {
    refreshTokenRepository.deleteByMemberId(memberId);
  }

  public void validateRefreshTokenExist(Long memberId, String refresh) {
    RefreshToken storedRefreshToken = get(memberId);

    // 만료된 토큰이면 삭제 후 예외 발생
    if (storedRefreshToken.getExpiredTime().isBefore(Instant.now())) {
      delete(memberId);
      throw new RefreshTokenNotFoundException(memberId);
    }

    if (Objects.equals(refresh, storedRefreshToken.getToken()) == false) {
      throw new RefreshTokenNotFoundException(memberId);
    }
  }
}
