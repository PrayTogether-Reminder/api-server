package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentResponse;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 내용 조회 테스트")
public class PrayerContentFetchIntegrateTest extends IntegrateTest {

  private HttpHeaders headers;
  private Member member;
  private Room room;
  private PrayerTitle prayerTitle;
  private final int TEST_CNT = 5;

  @BeforeEach
  void setup() {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    // 방 생성
    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    // 회원-방 연관관계 생성
    MemberRoom memberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(member, room);
    memberRoomRepository.save(memberRoom);

    // 기도 제목 생성
    prayerTitle = testUtils.createUniquePrayerTitle_With_Room(room);
    prayerTitleRepository.save(prayerTitle);

    // 기도 내용 추가
    PrayerContent prayerContent =
        PrayerContent.builder()
            .prayerTitle(prayerTitle)
            .content("test-prayer-content")
            .memberId(member.getId())
            .memberName(member.getName())
            .build();
    prayerContentRepository.save(prayerContent);
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
  }

  @Test
  @DisplayName("기도 제목에 해당하는 기도 내용 목록을 조회하여 200 OK 응답")
  void fetch_prayer_contents_list_then_return_200_ok() {
    // given
    // 회원 및 기도 내용 추가
    for (int i = 1; i < TEST_CNT; i++) {
      Member newMember = testUtils.createUniqueMember();
      memberRepository.save(newMember);

      PrayerContent prayerContent =
          PrayerContent.builder()
              .prayerTitle(prayerTitle)
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
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<PrayerContentResponse> responseEntity =
        restTemplate.exchange(uri, HttpMethod.GET, requestEntity, PrayerContentResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 내용 목록 조회 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);
    PrayerContentResponse response = responseEntity.getBody();
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
