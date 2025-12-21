package site.praytogether.pray_together.test_config;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.prayer.domain.PrayerCompletion;
import site.praytogether.pray_together.domain.prayer.domain.PrayerCompletionRepository;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;

/**
 * 기도 완료 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestPrayerCompletionUtils {

  private final PrayerCompletionRepository prayerCompletionRepository;

  /**
   * 특정 멤버의 기도 완료를 여러 번 생성
   *
   * @param memberId 멤버 ID
   * @param prayerTitle 기도 제목
   * @param count 생성할 기도 완료 횟수
   */
  public void create(Long memberId, PrayerTitle prayerTitle, int count) {
    for (int i = 0; i < count; i++) {
      PrayerCompletion completion = PrayerCompletion.create(memberId, prayerTitle);
      prayerCompletionRepository.save(completion);
    }
  }
}
