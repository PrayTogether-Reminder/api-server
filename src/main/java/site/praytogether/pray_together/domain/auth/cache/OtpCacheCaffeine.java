package site.praytogether.pray_together.domain.auth.cache;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.Optional;
import site.praytogether.pray_together.domain.auth.exception.OtpNotFoundException;

public class OtpCacheCaffeine implements OtpCache {

  private final Cache<String, String> cache;

  public OtpCacheCaffeine(Cache<String, String> otpCache) {
    cache = otpCache;
  }

  @Override
  public void put(String key, String value) {
    // old value is replaced by the new value
    cache.put(key, value);
  }

  @Override
  public String delete(String key) {
    cache.invalidate(key);
    return key;
  }

  @Override
  public String get(String key) {
    Optional<String> ifPresent = Optional.ofNullable(cache.getIfPresent(key));
    return ifPresent.orElseThrow(() -> new OtpNotFoundException(key));
  }
}
