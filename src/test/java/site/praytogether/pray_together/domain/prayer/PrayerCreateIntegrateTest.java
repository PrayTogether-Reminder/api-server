package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 작성 통합 테스트")
public class PrayerCreateIntegrateTest extends IntegrateTest {

  private Member member;
  private Room room;
  private MemberRoom memberRoom;
  private String token;

  private static Long validRoomId;
  private static Long validMemberId;

  @BeforeEach
  void setup() {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);
    validMemberId = member.getId();

    // 방 생성
    room = testUtils.createUniqueRoom();
    roomRepository.save(room);
    validRoomId = room.getId();

    // 방 연관관계 생성
    memberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(member, room);
    memberRoomRepository.save(memberRoom);

    // 인증 토큰 생성
    token = testUtils.createBearerToken(member);
  }

  @Test
  @DisplayName("기도 제목 생성 시 201 Created 응답")
  void create_prayer_title_then_return_201_created() throws Exception {
    // given
    PrayerTitleCreateRequest titleRequest =
        PrayerTitleCreateRequest.builder()
            .title("test-prayer-title")
            .roomId(room.getId())
            .build();

    // when & then
    mockMvc.perform(post(PRAYERS_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(titleRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.title").value("test-prayer-title"))
        .andExpect(jsonPath("$.createdTime").exists());

    // DB 검증
    List<PrayerTitle> allTitle = prayerTitleRepository.findAll();
    assertThat(allTitle.size()).as("저장된 기도 제목의 개수가 예상과 다릅니다.").isEqualTo(1);
    assertThat(allTitle.get(0).getTitle()).as("제목이 올바르게 저장되지 않았습니다.").isEqualTo("test-prayer-title");
  }

  @Test
  @DisplayName("기도 내용 생성 시 201 Created 응답")
  void create_prayer_content_then_return_201_created() throws Exception {
    // given
    // 먼저 기도 제목 생성
    PrayerTitle prayerTitle = PrayerTitle.create(room, "test-prayer-title");
    prayerTitleRepository.save(prayerTitle);

    String newContent = "test-prayer-content";
    PrayerContentCreateRequest contentRequest =
        PrayerContentCreateRequest.builder()
            .memberId(member.getId())
            .memberName(member.getName())
            .content(newContent)
            .build();

    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";

    // when & then
    mockMvc.perform(post(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(contentRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").exists());

    // DB 검증
    List<PrayerContent> allContent = prayerContentRepository.findAll();
    assertThat(allContent.size()).as("저장된 기도 내용의 개수가 예상과 다릅니다.").isEqualTo(1);
    assertThat(allContent.get(0).getContent()).isEqualTo(newContent);
  }

  @DisplayName("기도 제목 생성 요청 유효성 검사 실패 시 400 Bad Request 응답")
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("provideInvalidPrayerTitleCreateArguments")
  void create_prayer_title_with_invalid_input_then_return_400_bad_request(
      String test, Long roomId, String title) throws Exception {

    // given
    PrayerTitleCreateRequest requestDto =
        PrayerTitleCreateRequest.builder().roomId(roomId).title(title).build();

    // when & then
    mockMvc.perform(post(PRAYERS_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest());

    // DB 검증
    List<PrayerTitle> allTitle = prayerTitleRepository.findAll();
    assertThat(allTitle.size()).as(test + ": 예외 발생 시 기도 제목이 저장되면 안됩니다.").isZero();
  }
  
  @DisplayName("기도 내용 생성 요청 유효성 검사 실패 시 400 Bad Request 응답")
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("provideInvalidPrayerContentCreateArguments")
  void create_prayer_content_with_invalid_input_then_return_400_bad_request(
      String test, String memberName, String content) throws Exception {

    // given
    // 먼저 기도 제목 생성
    PrayerTitle prayerTitle = PrayerTitle.create(room, "test-prayer-title");
    prayerTitleRepository.save(prayerTitle);

    PrayerContentCreateRequest requestDto =
        PrayerContentCreateRequest.builder()
            .memberId(member.getId())
            .memberName(memberName)
            .content(content)
            .build();
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";

    // when & then
    mockMvc.perform(post(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest());

    // DB 검증
    List<PrayerContent> allContent = prayerContentRepository.findAll();
    assertThat(allContent.size()).as(test + ": 예외 발생 시 기도 내용이 저장되면 안됩니다.").isZero();
  }

  private static Stream<Arguments> provideInvalidPrayerTitleCreateArguments() {
    String validTitle = "valid-title";

    return Stream.of(
        // --- roomId 유효성 검사 ---
        Arguments.of("roomId가 0일 때", 0L, validTitle),
        Arguments.of("roomId가 음수일 때", -1L, validTitle),
        Arguments.of("roomId가 null일 때", null, validTitle),

        // --- title 유효성 검사 ---
        Arguments.of("title이 ''일 때", validRoomId, ""),
        Arguments.of("title이 null일 때", validRoomId, null),
        Arguments.of("title이 50자 초과일 때", validRoomId, "a".repeat(51))
    );
  }
  
  private static Stream<Arguments> provideInvalidPrayerContentCreateArguments() {
    String validMemberName = "valid-name";
    String validContent = "valid-content";

    return Stream.of(
        // --- content 유효성 검사 ---
        Arguments.of("content가 '' 일 때 (empty)", validMemberName, ""),
        Arguments.of("content가 null일 때", validMemberName, null),
        
        // --- memberName 유효성 검사 ---
        Arguments.of("memberName이 '' 일 때 (empty)", "", validContent),
        Arguments.of("memberName이 null일 때", null, validContent)
    );
  }
}
