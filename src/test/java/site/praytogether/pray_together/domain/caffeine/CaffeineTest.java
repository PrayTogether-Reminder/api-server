package site.praytogether.pray_together.domain.caffeine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static site.praytogether.pray_together.constant.CoreConstant.OtpConstant.OTP_TTL_MINUTE;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.auth.cache.OtpCacheCaffeine;
import site.praytogether.pray_together.domain.auth.exception.OtpNotFoundException;

@DisplayName("OTP 캐시 (Caffeine) 단위 테스트")
class CaffeineTest {

  private OtpCacheCaffeine otpCache;
  private Ticker ticker;

  private final String TEST_EMAIL = "test@example.com";
  private final String TEST_OTP = "123456";
  private final String TEST_OTP_UPDATED = "654321";

  @BeforeEach
  void setUp() {
    ticker = mock(Ticker.class);

    otpCache =
        new OtpCacheCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(OTP_TTL_MINUTE, TimeUnit.MINUTES)
                .ticker(ticker)
                .build());
  }

  @Test
  @DisplayName("캐시 저장 후 만료 시간이 지나면 OtpNotFoundException 예외가 발생해야 함")
  void when_cache_expired_then_throw_exception() {
    // given
    otpCache.put(TEST_EMAIL, TEST_OTP);

    // 캐시 만료 설정
    doReturn(TimeUnit.MINUTES.toNanos(OTP_TTL_MINUTE) + 100).when(ticker).read();

    // when & then
    // 만료된 캐시에서 값을 가져오려고 하면 OtpNotFoundException 발생해야 함
    assertThatThrownBy(() -> otpCache.get(TEST_EMAIL))
        .as("만료된 캐시에 접근 시 OtpNotFoundException이 발생해야 합니다.")
        .isInstanceOf(OtpNotFoundException.class);
  }

  @Test
  @DisplayName("같은 키로 새로운 값 저장 시 이전 값은 제거되고 새 값만 유지되어야 함")
  void when_cache_overwritten_then_return_latest_value() {
    // given
    otpCache.put(TEST_EMAIL, TEST_OTP);

    // when
    // 같은 키로 새로운 값 저장
    otpCache.put(TEST_EMAIL, TEST_OTP_UPDATED);

    // then
    // 새로운 값만 조회되어야 함
    String otp = otpCache.get(TEST_EMAIL);
    assertThat(otp)
        .as("같은 키로 저장한 최신 OTP 값이 조회되어야 합니다.")
        .isEqualTo(TEST_OTP_UPDATED)
        .as("이전에 저장한 OTP 값이 남아있으면 안됩니다.")
        .isNotEqualTo(TEST_OTP);
  }

  @Test
  @DisplayName("캐시에서 키를 삭제하면 해당 데이터는 더 이상 접근할 수 없어야 함")
  void when_cache_deleted_then_throw_exception() {
    // given
    otpCache.put(TEST_EMAIL, TEST_OTP);

    // when
    otpCache.delete(TEST_EMAIL);

    // then
    assertThatThrownBy(() -> otpCache.get(TEST_EMAIL))
        .as("삭제된 캐시 키에 접근 시 OtpNotFoundException이 발생해야 합니다.")
        .isInstanceOf(OtpNotFoundException.class);
  }

  @Test
  @DisplayName("캐시에 저장한 데이터는 만료 전까지 정상적으로 조회되어야 함")
  void when_cache_valid_then_return_value() {
    // given
    otpCache.put(TEST_EMAIL, TEST_OTP);

    // when
    String retrievedOtp = otpCache.get(TEST_EMAIL);

    // then
    assertThat(retrievedOtp).as("저장한 OTP 값이 정확히 조회되어야 합니다.").isEqualTo(TEST_OTP);
  }
}
