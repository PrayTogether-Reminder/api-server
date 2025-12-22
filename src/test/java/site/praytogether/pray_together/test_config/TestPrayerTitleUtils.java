package site.praytogether.pray_together.test_config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitleRepository;
import site.praytogether.pray_together.domain.room.model.Room;

/**
 * 기도 제목 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestPrayerTitleUtils {

  private final PrayerTitleRepository prayerTitleRepository;
  private static int prayerTitleUniqueId = 0;

  /**
   * 기도 제목 생성
   *
   * @param room 기도 제목이 속할 방
   * @return 생성된 기도 제목
   */
  public PrayerTitle createSave(Room room) {
    PrayerTitle prayerTitle = PrayerTitle.create(
        room,
        "test-prayer-title" + prayerTitleUniqueId++);
    prayerTitleRepository.save(prayerTitle);
    return prayerTitle;
  }
}
