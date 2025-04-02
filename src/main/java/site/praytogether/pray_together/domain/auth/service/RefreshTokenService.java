package site.praytogether.pray_together.domain.auth.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.auth.cache.RefreshTokenCache;
import site.praytogether.pray_together.domain.auth.exception.RefreshTokenNotFoundException;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  private final RefreshTokenCache cache;

  public void save(String key, String token) {
    cache.save(key, token);
  }

  public String get(String key) {
    return cache.get(key);
  }

  public String delete(String key) {
    return cache.delete(key);
  }

  public void validateRefreshTokenExist(String memberId, String refresh) {
    String cachedRefresh = cache.get(memberId);
    if (Objects.equals(refresh, cachedRefresh) == false) {
      throw new RefreshTokenNotFoundException(Long.valueOf(memberId));
    }
  }
}
