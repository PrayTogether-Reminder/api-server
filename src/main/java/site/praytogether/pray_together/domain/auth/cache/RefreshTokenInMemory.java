package site.praytogether.pray_together.domain.auth.cache;

import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenInMemory implements RefreshTokenCache {
  private final Map<String, String> cache;

  @Override
  public void save(String key, String value) {
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

  @Override
  public boolean isExist(String key) {
    return cache.containsKey(key);
  }
}
