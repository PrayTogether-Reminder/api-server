package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;

import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.presentation.dto.response.PrayerContentResponse;
import site.praytogether.pray_together.domain.prayer.domain.PrayerContent;
import site.praytogether.pray_together.domain.prayer.domain.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.domain.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 내용 조회 테스트")
public class PrayerContentFetchIntegrateTest extends IntegrateTest {

  private String token;
  private Member member;
  private Room room;
  private PrayerTitle prayerTitle;
  private final int TEST_CNT = 5;

  @BeforeEach
  void setup() {
    // 회원 생성
    member = testMemberUtils.createSave();

    // 방 생성
    room = testRoomUtils.createSave();

    // 회원-방 연관관계 생성
    testMemberRoomUtils.createSaveMemberRoom_With_MemberOwner_AND_Room(member, room);

    // 기도 제목 생성
    prayerTitle = testPrayerTitleUtils.createSave(room);

    // 기도 내용 추가
    PrayerContent prayerContent =
        PrayerContent.builder()
            .prayerTitle(prayerTitle)
            .writerId(member.getId())
            .writerName(member.getName())
            .content("test-prayer-content")
            .memberId(member.getId())
            .memberName(member.getName())
            .build();
    prayerContentRepository.save(prayerContent);
    token = testAuthUtils.createBearerToken(member);
  }

  @Test
  @DisplayName("기도 제목에 해당하는 기도 내용 목록을 조회하여 200 OK 응답")
  void fetch_prayer_contents_list_then_return_200_ok() throws Exception {
    // given
    // 회원 및 기도 내용 추가
    for (int i = 1; i < TEST_CNT; i++) {
      Member newMember = testMemberUtils.createSave();

      PrayerContent prayerContent =
          PrayerContent.builder()
              .prayerTitle(prayerTitle)
              .writerId(newMember.getId())
              .writerName(newMember.getName())
              .content("test-prayer-content" + (i + 'ㄱ'))
              .memberId(newMember.getId())
              .memberName(newMember.getName())
              .build();
      prayerContentRepository.save(prayerContent);
    }
    String uri =
        UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .path("/{titleId}/contents")
            .buildAndExpand(prayerTitle.getId())
            .toUriString();

    // when
    MvcResult result = mockMvc.perform(get(uri)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    PrayerContentResponse response = objectMapper.readValue(responseBody, PrayerContentResponse.class);
    assertThat(response).as("기도 내용 목록 조회 API 응답 결과가 NULL 입니다.").isNotNull();

    List<PrayerContentInfo> prayerContents = response.getPrayerContents();
    assertThat(prayerContents.size())
        .as("기도 내용 목록 조회 API 응답 결과 데이터 개수가 기대값과 다릅니다.")
        .isEqualTo(TEST_CNT);

    assertThat(prayerContents)
        .as("기도 내용 목록이 memberName 기준으로 오름차순 정렬되지 않았습니다.")
        .isSortedAccordingTo(Comparator.comparing(PrayerContentInfo::getMemberName));
  }
}
