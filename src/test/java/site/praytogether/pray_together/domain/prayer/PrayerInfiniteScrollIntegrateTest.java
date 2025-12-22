package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.PRAYER_TITLES_INFINITE_SCROLL_SIZE;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.prayer.presentation.dto.response.PrayerTitleInfiniteScrollResponse;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.presentation.dto.PrayerTitleInfoDto;
import site.praytogether.pray_together.domain.prayer.presentation.dto.PrayerCompletionCountDto;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 제목 무한 스크롤 테스트")
public class PrayerInfiniteScrollIntegrateTest extends IntegrateTest {

  private String token;
  private Member member;
  private Room room;
  private final int TEST_CNT = PRAYER_TITLES_INFINITE_SCROLL_SIZE * 3;

  private final String AFTER = "after";
  private final String ROOM_ID = "roomId";

  @BeforeEach
  void setup() {
    member = testMemberUtils.createSave();

    room = testRoomUtils.createSave();

    testMemberRoomUtils.createSaveMemberRoom_With_MemberOwner_AND_Room(member, room);

    testPrayerTitleUtils.createSave(room);

    for (int i = 0; i < TEST_CNT; i++) {
      testPrayerTitleUtils.createSave(room);
    }
    token = testAuthUtils.createBearerToken(member);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("providePrayerInfiniteScrollParameters")
  @DisplayName("다양한 파라미터 조합 요청시 기본값으로 정상 처리되어 200 OK 응답")
  void fetch_prayer_contents_list_with_default_values_for_different_params_then_return_200_ok(
      String test, String after) throws Exception {

    // given
    String uri =
        UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .queryParam(ROOM_ID, room.getId())
            .queryParam(AFTER, after)
            .toUriString();

    // when
    MvcResult result = mockMvc.perform(get(uri)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    PrayerTitleInfiniteScrollResponse response = objectMapper.readValue(responseBody, PrayerTitleInfiniteScrollResponse.class);
    assertThat(response).as(test + ": 기도 내용 목록 무한 스크롤 API 응답 결과가 NULL 입니다.").isNotNull();

    List<PrayerTitleInfoDto> titles = response.getPrayerTitles();
    assertThat(titles.size())
        .as(test + ": 기도 내용 목록 무한 스크롤 API 응답 결과 데이터가 없습니다.")
        .isEqualTo(PRAYER_TITLES_INFINITE_SCROLL_SIZE);

    assertThat(titles)
        .as(test + ": 기도 내용 목록이 createdTime 기준으로 내림차순 정렬되지 않았습니다.")
        .isSortedAccordingTo(Comparator.comparing(PrayerTitleInfoDto::getCreatedTime).reversed());

    int repeatCount = 1;
    while (!titles.isEmpty()) {

      // next given
      PrayerTitleInfoDto lastTitle = titles.get(titles.size() - 1);
      Instant lastAfter = lastTitle.getCreatedTime();

      uri =
          UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
              .queryParam(ROOM_ID, room.getId())
              .queryParam(AFTER, lastAfter)
              .build()
              .toUriString();

      // next when
      result = mockMvc.perform(get(uri)
              .header(HttpHeaders.AUTHORIZATION, token))
          .andExpect(status().isOk())
          .andReturn();

      // next then
      responseBody = result.getResponse().getContentAsString();
      response = objectMapper.readValue(responseBody, PrayerTitleInfiniteScrollResponse.class);
      assertThat(response).as(test + ": %d 번째 요청 응답 body가 null입니다.", repeatCount).isNotNull();
      titles = response.getPrayerTitles();
    }

    // --- 최종 검증 ---
    assertThat(titles).as(test + ": 마지막 요청 결과가 빈 리스트가 아닙니다.").isEmpty();
  }

  private static Stream<Arguments> providePrayerInfiniteScrollParameters() {
    return Stream.of(
        // 기본값 테스트 (after=0)
        Arguments.of("after=0", "0"),
        Arguments.of("after null", null),
        Arguments.of("after 빈값", ""));
  }

  @Test
  @DisplayName("회원 탈퇴한 멤버의 기도 완료 정보는 memberId는 유지, memberName은 '알 수 없음'으로 표시")
  void deleted_member_prayer_completion_shows_id_and_unknown_name() throws Exception {
    // given
    Member[] members = testMemberRoomUtils.createMembersAndJoinRoom(room, 3);
    Member member1 = members[0];
    Member member2 = members[1];
    Long deletedMemberId = member2.getId();

    PrayerTitle prayerTitle = testPrayerTitleUtils.createSave(room);
    testPrayerCompletionUtils.createSave(member1.getId(), prayerTitle, 3);
    testPrayerCompletionUtils.createSave(member2.getId(), prayerTitle, 2);

    memberRepository.delete(member2);
    // when
    String uri =
        UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .queryParam(ROOM_ID, room.getId())
            .queryParam(AFTER, "0")
            .toUriString();

    MvcResult result = mockMvc.perform(get(uri)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    PrayerTitleInfiniteScrollResponse response =
        objectMapper.readValue(responseBody, PrayerTitleInfiniteScrollResponse.class);
    List<PrayerTitleInfoDto> titles = response.getPrayerTitles();

    // then
    PrayerTitleInfoDto titleDto = titles.stream()
        .filter(t -> t.getId().equals(prayerTitle.getId()))
        .findFirst()
        .orElseThrow(
            () -> new AssertionError("기도 제목을 찾을 수 없습니다. ID: " + prayerTitle.getId()));

    List<PrayerCompletionCountDto> prayers = titleDto.getPrayers();
    assertThat(prayers).hasSize(2);

    PrayerCompletionCountDto deletedMemberPrayer = prayers.stream()
        .filter(p -> p.getMemberName().equals("알 수 없음"))
        .findFirst()
        .orElseThrow(
            () ->
                new AssertionError(
                    "기도 완료 정보를 찾을 수 없습니다. memberName: 알 수 없음"));

    assertThat(deletedMemberPrayer.getMemberId()).isEqualTo(deletedMemberId);
    assertThat(deletedMemberPrayer.getMemberName()).isEqualTo("알 수 없음");
    assertThat(deletedMemberPrayer.getPrayerCount()).isEqualTo(2L);
  }

  @Test
  @DisplayName("기도 제목별로 멤버의 기도 횟수가 정확하게 집계됨")
  void prayer_count_is_accurately_aggregated_by_title_and_member() throws Exception {
    // given
    Member[] members = testMemberRoomUtils.createMembersAndJoinRoom(room, 3);
    Member member1 = members[0];
    Member member2 = members[1];
    Member member3 = members[2];

    PrayerTitle prayerTitle1 = testPrayerTitleUtils.createSave(room);
    PrayerTitle prayerTitle2 = testPrayerTitleUtils.createSave(room);

    // prayerTitle1: member1(3번), member2(2번), member3(1번)
    testPrayerCompletionUtils.createSave(member1.getId(), prayerTitle1, 3);
    testPrayerCompletionUtils.createSave(member2.getId(), prayerTitle1, 2);
    testPrayerCompletionUtils.createSave(member3.getId(), prayerTitle1, 1);

    // prayerTitle2: member1(1번), member2(1번)
    testPrayerCompletionUtils.createSave(member1.getId(), prayerTitle2, 1);
    testPrayerCompletionUtils.createSave(member2.getId(), prayerTitle2, 1);

    // when
    String uri =
        UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .queryParam(ROOM_ID, room.getId())
            .queryParam(AFTER, "0")
            .toUriString();

    MvcResult result = mockMvc.perform(get(uri)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    PrayerTitleInfiniteScrollResponse response =
        objectMapper.readValue(responseBody, PrayerTitleInfiniteScrollResponse.class);
    List<PrayerTitleInfoDto> titles = response.getPrayerTitles();

    // then - prayerTitle1 검증
    long pt1Member1Count = 3L;
    long pt1Member2Count = 2L;
    long pt1Member3Count = 1L;
    int pt1PrayerSize = 3;

    PrayerTitleInfoDto title1Dto = findPrayerTitleInfo(titles, prayerTitle1);
    assertThat(title1Dto.getPrayers()).hasSize(pt1PrayerSize);
    assertPrayerCount(titles, prayerTitle1, member1.getId(), pt1Member1Count);
    assertPrayerCount(titles, prayerTitle1, member2.getId(), pt1Member2Count);
    assertPrayerCount(titles, prayerTitle1, member3.getId(), pt1Member3Count);

    // then - prayerTitle2 검증
    long pt2Member1Count = 1L;
    long pt2Member2Count = 1L;
    int pt2PrayerSize = 2;

    PrayerTitleInfoDto title2Dto = findPrayerTitleInfo(titles, prayerTitle2);
    assertThat(title2Dto.getPrayers()).hasSize(pt2PrayerSize);
    assertPrayerCount(titles, prayerTitle2, member1.getId(), pt2Member1Count);
    assertPrayerCount(titles, prayerTitle2, member2.getId(), pt2Member2Count);
  }

  private PrayerTitleInfoDto findPrayerTitleInfo(
      List<PrayerTitleInfoDto> titles,
      PrayerTitle targetTitle) {
    return titles.stream()
        .filter(t -> t.getId().equals(targetTitle.getId()))
        .findFirst()
        .orElseThrow(
            () -> new AssertionError("기도 제목을 찾을 수 없습니다. ID: " + targetTitle.getId()));
  }

  private void assertPrayerCount(
      List<PrayerTitleInfoDto> titles,
      PrayerTitle targetTitle,
      Long memberId,
      Long expectedCount) {
    PrayerTitleInfoDto titleDto = findPrayerTitleInfo(titles, targetTitle);
    PrayerCompletionCountDto memberPrayer = titleDto.getPrayers().stream()
        .filter(p -> p.getMemberId().equals(memberId))
        .findFirst()
        .orElseThrow(
            () -> new AssertionError("기도 완료 정보를 찾을 수 없습니다. memberId: " + memberId));

    assertThat(memberPrayer.getPrayerCount()).isEqualTo(expectedCount);
  }
}
