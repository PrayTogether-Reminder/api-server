package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.exception.ExceptionResponse;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 삭제 통합 테스트")
public class PrayerDeleteIntegrateTest extends IntegrateTest {

  private Member member;
  private Room room;
  private MemberRoom memberRoom;
  private HttpHeaders headers;
  private PrayerTitle prayerTitle;
  private PrayerContent prayerContent;

  @BeforeEach
  void setup() {
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
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);

    // 기도 제목 생성
    prayerTitle = PrayerTitle.create(room, "test-prayer-changedTitle");
    prayerTitleRepository.save(prayerTitle);

    // 기도 내용 생성
    for (int i = 0; i < 5; i++) {
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

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @Test
  @DisplayName("기도 제목 삭제 시 제목+내용 삭제 후 200 OK 응답")
  void delete_prayer_then_return_200_ok() {
    // given
    HttpEntity<Void> deleteRequestEntity = new HttpEntity<>(headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.DELETE, deleteRequestEntity, MessageResponse.class);

    // then
    // 삭제 응답 상태 검증
    assertThat(responseEntity.getStatusCode())
        .as("기도 삭제 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

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
  @DisplayName("기도 삭제 시 유효하지 않은 ID인 경우 400 Bad Request 응답")
  void delete_prayer_with_invalid_id_then_return_400_bad_request(String test, String encodedUrl) {
    // given
    HttpEntity<Void> deleteRequestEntity = new HttpEntity<>(headers);
    String url = PRAYERS_API_URL + "/" + encodedUrl;

    // when
    ResponseEntity<ExceptionResponse> response =
        restTemplate.exchange(url, HttpMethod.DELETE, deleteRequestEntity, ExceptionResponse.class);

    // then
    assertThat(response.getStatusCode())
        .as("유효하지 않은 ID로 기도 삭제 요청 시 400 Bad Request가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.BAD_REQUEST);

    ExceptionResponse exceptionResponse = response.getBody();
    assertThat(exceptionResponse).isNotNull();
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
  @DisplayName("다른 방의 회원이 기도 삭제 요청 시 404 Not Found 응답")
  void delete_prayer_by_member_from_different_room_then_return_404_not_found() {
    // given
    // 새로운 회원 생성
    Member anotherMember = testUtils.createUniqueMember();
    memberRepository.save(anotherMember);

    // 새로운 회원의 인증 헤더 생성
    HttpHeaders anotherHeaders = testUtils.create_Auth_HttpHeader_With_Member(anotherMember);
    HttpEntity<Void> deleteRequestEntity = new HttpEntity<>(anotherHeaders);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when
    ResponseEntity<ExceptionResponse> response =
        restTemplate.exchange(url, HttpMethod.DELETE, deleteRequestEntity, ExceptionResponse.class);

    // then
    assertThat(response.getStatusCode())
        .as("다른 방의 회원이 기도 삭제 요청 시 404 Not Found가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.NOT_FOUND);

    ExceptionResponse exceptionResponse = response.getBody();
    assertThat(exceptionResponse).isNotNull();
  }
}
