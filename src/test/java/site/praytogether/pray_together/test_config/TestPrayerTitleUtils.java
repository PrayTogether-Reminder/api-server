package site.praytogether.pray_together.test_config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitleRepository;
import site.praytogether.pray_together.domain.prayer.presentation.dto.PrayerTitleInfoDto;
import site.praytogether.pray_together.domain.prayer.presentation.dto.response.PrayerTitleInfiniteScrollResponse;
import site.praytogether.pray_together.domain.room.model.Room;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 기도 제목 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestPrayerTitleUtils {

  private final PrayerTitleRepository prayerTitleRepository;
  private final TestUtils testUtils;
  private final ObjectMapper objectMapper;

  private static final String PRAYERS_API_URL = "/api/v1/prayers";
  private static final String ROOM_ID = "roomId";
  private static final String AFTER = "after";

  /**
   * 기도 제목 생성
   *
   * @param room 기도 제목이 속할 방
   * @return 생성된 기도 제목
   */
  public PrayerTitle create(Room room) {
    PrayerTitle prayerTitle = testUtils.createUniquePrayerTitle_With_Room(room);
    prayerTitleRepository.save(prayerTitle);
    return prayerTitle;
  }
}
