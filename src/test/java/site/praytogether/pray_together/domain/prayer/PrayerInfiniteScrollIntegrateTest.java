package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;
import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.PRAYER_TITLES_INFINITE_SCROLL_SIZE;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerScrollResponse;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitleInfo;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 제목 무한 스크롤 테스트")
public class PrayerInfiniteScrollIntegrateTest extends IntegrateTest {

  private HttpHeaders headers;
  private Member member;
  private Room room;
  private PrayerTitle prayerTitle;
  private final int TEST_CNT = PRAYER_TITLES_INFINITE_SCROLL_SIZE * 3;

  private final String AFTER = "after";
  private final String ROOM_ID = "roomId";

  @BeforeEach
  void setup() {
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    MemberRoom memberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(member, room);
    memberRoomRepository.save(memberRoom);

    prayerTitle = PrayerTitle.create(room, "test-title");
    prayerTitleRepository.save(prayerTitle);

    for (int i = 0; i < TEST_CNT; i++) {
      PrayerTitle prayerTitle = PrayerTitle.create(room, "test-title" + i);
      prayerTitleRepository.save(prayerTitle);
    }
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("providePrayerScrollParameters")
  @DisplayName("다양한 파라미터 조합 요청시 기본값으로 정상 처리되어 200 OK 응답")
  void fetch_prayer_contents_list_with_default_values_for_different_params_then_return_200_ok(
      String test, String after) {

    // given
    String uri =
        UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .queryParam(ROOM_ID, room.getId())
            .queryParam(AFTER, after)
            .toUriString();
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<PrayerScrollResponse> responseEntity =
        restTemplate.exchange(uri, HttpMethod.GET, requestEntity, PrayerScrollResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as(test + ": 기도 내용 목록 무한 스크롤 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);
    PrayerScrollResponse response = responseEntity.getBody();
    assertThat(response).as(test + ": 기도 내용 목록 무한 스크롤 API 응답 결과가 NULL 입니다.").isNotNull();

    List<PrayerTitleInfo> titles = response.getPrayerTitles();
    assertThat(titles.size())
        .as(test + ": 기도 내용 목록 무한 스크롤 API 응답 결과 데이터가 없습니다.")
        .isEqualTo(PRAYER_TITLES_INFINITE_SCROLL_SIZE);

    assertThat(titles)
        .as(test + ": 기도 내용 목록이 createdTime 기준으로 내림차순 정렬되지 않았습니다.")
        .isSortedAccordingTo(Comparator.comparing(PrayerTitleInfo::getCreatedTime).reversed());

    int repeatCount = 1;
    while (!titles.isEmpty()) {

      // next given
      PrayerTitleInfo lastTitle = titles.get(titles.size() - 1);
      Instant lastAfter = lastTitle.getCreatedTime();

      uri =
          UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
              .queryParam(ROOM_ID, room.getId())
              .queryParam(AFTER, lastAfter)
              .build()
              .toUriString();

      // next when
      responseEntity =
          restTemplate.exchange(uri, HttpMethod.GET, requestEntity, PrayerScrollResponse.class);

      // next then
      assertThat(responseEntity.getStatusCode())
          .as(test + ": %d 번째 요청 응답 코드가 200 OK 아닙니다.", repeatCount)
          .isEqualTo(HttpStatus.OK);

      response = responseEntity.getBody();
      assertThat(response).as(test + ": %d 번째 요청 응답 body가 null입니다.", repeatCount).isNotNull();
      titles = response.getPrayerTitles();
    }

    // --- 최종 검증 ---
    assertThat(titles).as(test + ": 마지막 요청 결과가 빈 리스트가 아닙니다.").isEmpty();
  }

  private static Stream<Arguments> providePrayerScrollParameters() {
    return Stream.of(
        // 기본값 테스트 (after=0)
        Arguments.of("after=0", "0"),
        Arguments.of("after null", null),
        Arguments.of("after 빈값", ""));
  }
}
