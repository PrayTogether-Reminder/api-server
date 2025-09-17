package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import org.springframework.http.HttpHeaders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 삭제 통합 테스트")
public class PrayerDeleteIntegrateTest extends IntegrateTest {

  private Member member;
  private Room room;
  private MemberRoom memberRoom;
  private String token;
  private PrayerTitle prayerTitle;
  private PrayerContent prayerContent;
  private final int PRAYER_CONTENT_COUNT = 5;

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

    // 인증 헤더 생성
    token = testUtils.createBearerToken(member);

    // 기도 제목 생성
    prayerTitle = PrayerTitle.create(room, "test-prayer-changedTitle");
    prayerTitleRepository.save(prayerTitle);

    // 기도 내용 생성
    for (int i = 0; i < PRAYER_CONTENT_COUNT; i++) {
      Member newMember = testUtils.createUniqueMember();
      memberRepository.save(newMember);

      prayerContent =
          PrayerContent.create(
              prayerTitle,
              PrayerContentCreateRequest.builder()
                  .memberId(newMember.getId())
                  .memberName(newMember.getName())
                  .content("test-prayer-content" + i)
                  .build());

      prayerTitle.addContent(prayerContent);
      prayerContentRepository.save(prayerContent);
    }
  }

  @Test
  @DisplayName("기도 제목 삭제 시 제목+내용 삭제 후 200 OK 응답")
  void delete_prayer_title_then_return_200_ok() throws Exception {
    // given
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when
    mockMvc.perform(delete(url)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());

    // then

    // 기도 제목이 삭제되었는지 확인
    assertThat(prayerTitleRepository.findById(prayerTitle.getId()))
        .as("기도 제목이 삭제되지 않았습니다.")
        .isEmpty();

    // 연관된 기도 내용이 삭제되었는지 확인
    List<PrayerContent> remainingContents = prayerContentRepository.findAll();
    assertThat(remainingContents).as("연관된 기도 내용이 삭제되지 않았습니다.").isEmpty();
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("provideInvalidPrayerDeleteParameters")
  @DisplayName("기도 제목 삭제 시 유효하지 않은 ID인 경우 400 Bad Request 응답")
  void delete_prayer_title_with_invalid_id_then_return_400_bad_request(String test, String encodedUrl) throws Exception {
    // given
    String url = PRAYERS_API_URL + "/" + encodedUrl;

    // when & then
    mockMvc.perform(delete(url)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isBadRequest());
  }

  private static Stream<Arguments> provideInvalidPrayerDeleteParameters() {
    return Stream.of(
        Arguments.of("음수 ID", URLEncoder.encode("-1", StandardCharsets.UTF_8)),
        Arguments.of("0 ID", URLEncoder.encode("0", StandardCharsets.UTF_8)),
        Arguments.of("문자열 ID", URLEncoder.encode("abc", StandardCharsets.UTF_8)),
        Arguments.of("특수문자 ID", URLEncoder.encode("!@#", StandardCharsets.UTF_8)),
        Arguments.of("소수점 ID", URLEncoder.encode("1.5", StandardCharsets.UTF_8)),
        Arguments.of("공백 ID", URLEncoder.encode(" ", StandardCharsets.UTF_8)),
        Arguments.of("null", "null") // null은 특별히 처리
        );
  }

  @Test
  @DisplayName("다른 방의 회원이 기도 제목 삭제 요청 시 404 Not Found 응답")
  void delete_prayer_title_by_member_from_different_room_then_return_404_not_found() throws Exception {
    // given
    // 새로운 회원 생성
    Member anotherMember = testUtils.createUniqueMember();
    memberRepository.save(anotherMember);

    // 새로운 회원의 인증 토큰 생성
    String anotherToken = testUtils.createBearerToken(anotherMember);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when & then
    mockMvc.perform(delete(url)
            .header(HttpHeaders.AUTHORIZATION, anotherToken))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("기도 내용 삭제 시 200 OK 응답")
  void delete_prayer_content_then_return_200_ok() throws Exception {
    // given
    List<PrayerContent> prayerContents = prayerContentRepository.findAll();
    Long contentId = prayerContents.get(0).getId();
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents/" + contentId;

    // when & then
    mockMvc.perform(delete(url)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk());
    prayerContentRepository.flush();
    
    Optional<PrayerContent> deletedContent = prayerContentRepository.findById(contentId);
    assertThat(deletedContent.isEmpty()).as("기도 내용 삭제가 적용되지 않았습니다.").isTrue();

     List<PrayerContent> all = prayerContentRepository.findAll();
      assertThat(all.size()).as("기도 내용 1건이 삭제되지 않았습니다.").isEqualTo(PRAYER_CONTENT_COUNT - 1);
  }
}
