package site.praytogether.pray_together.config;

import static site.praytogether.pray_together.constant.CoreConstant.OtpConstant.OTP_TTL_MINUTE;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OtpConfig {

  @Bean
  @Primary
  public Cache<String, String> otpCacheCaffeine() {
    return Caffeine.newBuilder().expireAfterWrite(OTP_TTL_MINUTE, TimeUnit.MINUTES).build();
  }
}
