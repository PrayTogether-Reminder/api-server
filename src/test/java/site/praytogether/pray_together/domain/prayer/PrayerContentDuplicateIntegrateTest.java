package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 내용 중복 검증 통합 테스트")
public class PrayerContentDuplicateIntegrateTest extends IntegrateTest {

  private Member member;
  private Room room;
  private MemberRoom memberRoom;
  private String token;
  private PrayerTitle prayerTitle;

  @BeforeEach
  void setup() throws Exception {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    // 방 생성
    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    // 방 연관관계 생성
    memberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(member, room);
    memberRoomRepository.save(memberRoom);

    // 인증 토큰 생성
    token = testUtils.createBearerToken(member);

    // 기도 제목 생성
    prayerTitle = PrayerTitle.create(room, "test-prayer-title");
    prayerTitleRepository.save(prayerTitle);
  }

  @Test
  @DisplayName("같은 이름으로 기도 내용 중복 생성 시 400 Bad Request 응답")
  void create_duplicate_prayer_content_then_return_400_bad_request() throws Exception {
    // given
    String memberName = "테스트사용자";

    // 첫 번째 기도 내용 생성
    PrayerContentCreateRequest firstRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(memberName)
        .content("첫 번째 기도 내용")
        .build();

    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";

    // when - 첫 번째 요청
    mockMvc.perform(post(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(firstRequest)))
        .andExpect(status().isCreated());

    // given - 같은 이름으로 두 번째 기도 내용 생성 시도
    PrayerContentCreateRequest secondRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(memberName)  // 같은 이름 사용
        .content("두 번째 기도 내용")
        .build();

    // when - 두 번째 요청 (400 Bad Request 기대)
    mockMvc.perform(post(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secondRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("PRAYER-003"))
        .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("이미 해당 기도 제목에 기도 내용을 작성하셨습니다")));
  }

  @Test
  @DisplayName("다른 이름으로 기도 내용 생성 시 201 Created 응답")
  void create_prayer_content_with_different_name_then_return_201_created() throws Exception {
    // given
    // 첫 번째 기도 내용 생성
    PrayerContentCreateRequest firstRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName("사용자1")
        .content("첫 번째 기도 내용")
        .build();

    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";

    // when - 첫 번째 요청
    mockMvc.perform(post(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(firstRequest)))
        .andExpect(status().isCreated());

    // given - 다른 이름으로 두 번째 기도 내용 생성
    PrayerContentCreateRequest secondRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName("사용자2")  // 다른 이름 사용
        .content("두 번째 기도 내용")
        .build();

    // when - 두 번째 요청
    mockMvc.perform(post(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secondRequest)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("다른 기도 제목에는 같은 이름으로 기도 내용 생성 가능")
  void create_prayer_content_with_same_name_in_different_title_then_return_201_created() throws Exception {
    // given
    String memberName = "테스트사용자";

    // 두 번째 기도 제목 생성
    PrayerTitle anotherPrayerTitle = PrayerTitle.create(room, "another-prayer-title");
    prayerTitleRepository.save(anotherPrayerTitle);

    // 첫 번째 기도 제목에 기도 내용 생성
    PrayerContentCreateRequest firstRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(memberName)
        .content("첫 번째 기도 내용")
        .build();

    String firstUrl = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";

    // when - 첫 번째 기도 제목에 생성
    mockMvc.perform(post(firstUrl)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(firstRequest)))
        .andExpect(status().isCreated());

    // given - 두 번째 기도 제목에 같은 이름으로 기도 내용 생성
    PrayerContentCreateRequest secondRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(memberName)  // 같은 이름 사용
        .content("두 번째 기도 내용")
        .build();

    String secondUrl = PRAYERS_API_URL + "/" + anotherPrayerTitle.getId() + "/contents";

    // when - 두 번째 기도 제목에 생성
    mockMvc.perform(post(secondUrl)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secondRequest)))
        .andExpect(status().isCreated());
  }
}