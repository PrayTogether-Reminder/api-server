package site.praytogether.pray_together.domain.auth.infrastructure.cache;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import site.praytogether.pray_together.domain.auth.domain.cache.OtpCache;

@RequiredArgsConstructor
public class OtpCacheInMemory implements OtpCache {
  private final Map<String, String> cache;

  @Override
  public void put(String key, String value) {
    cache.put(key, value);
  }

  @Override
  public String delete(String key) {
    return cache.remove(key);
  }

  @Override
  public String get(String key) {
    return cache.get(key);
  }
}
