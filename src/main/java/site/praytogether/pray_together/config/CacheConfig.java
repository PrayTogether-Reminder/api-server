package site.praytogether.pray_together.config;

import static site.praytogether.pray_together.constant.CoreConstant.OtpConstant.OTP_TTL_MINUTE;
import static site.praytogether.pray_together.constant.CoreConstant.OtpConstant.REFRESH_TOKEN_TTL_DAYS;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import site.praytogether.pray_together.domain.auth.domain.cache.OtpCache;
import site.praytogether.pray_together.domain.auth.infrastructure.cache.OtpCacheCaffeine;
import site.praytogether.pray_together.domain.auth.infrastructure.cache.OtpCacheInMemory;
import site.praytogether.pray_together.domain.auth.domain.cache.RefreshTokenCache;
import site.praytogether.pray_together.domain.auth.infrastructure.cache.RefreshTokenCaffeine;
import site.praytogether.pray_together.domain.auth.infrastructure.cache.RefreshTokenInMemory;

@Configuration
public class CacheConfig {

  @Bean
  @Primary
  public OtpCache otpCacheCaffeine() {
    return new OtpCacheCaffeine(
        Caffeine.newBuilder().expireAfterWrite(OTP_TTL_MINUTE, TimeUnit.MINUTES).build());
  }

  @Bean
  public OtpCache otpCacheInMemory() {
    return new OtpCacheInMemory(new ConcurrentHashMap<String, String>());
  }

  @Bean
  @Primary
  public RefreshTokenCache refreshTokenCaffeine() {
    return new RefreshTokenCaffeine(
        Caffeine.newBuilder().expireAfterWrite(REFRESH_TOKEN_TTL_DAYS, TimeUnit.DAYS).build());
  }

  @Bean
  public RefreshTokenCache refreshTokenInMemory() {
    return new RefreshTokenInMemory(new ConcurrentHashMap<String, String>());
  }
}
