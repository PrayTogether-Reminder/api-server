package site.praytogether.pray_together.domain.auth.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import site.praytogether.pray_together.domain.auth.domain.cache.RefreshTokenCache;
import site.praytogether.pray_together.domain.auth.domain.exception.RefreshTokenNotFoundException;

@RequiredArgsConstructor
public class RefreshTokenCaffeine implements RefreshTokenCache {

  private final Cache<String, String> cache;

  @Override
  public void save(String key, String value) {
    cache.put(key, value);
  }

  @Override
  public String delete(String key) {
    cache.invalidate(key);
    return key;
  }

  @Override
  public String get(String key) {
    return Optional.ofNullable(cache.getIfPresent(key))
        .orElseThrow(RefreshTokenNotFoundException::new);
  }

  @Override
  public boolean isExist(String key) {
    return cache.asMap().containsKey(key);
  }
}
