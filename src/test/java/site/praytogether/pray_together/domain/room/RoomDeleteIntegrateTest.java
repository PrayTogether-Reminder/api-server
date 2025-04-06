package site.praytogether.pray_together.domain.room;

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
import site.praytogether.pray_together.domain.room.dto.RoomCreateRequest;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.exception.ExceptionResponse;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("Room 삭제 통합 테스트")
public class RoomDeleteIntegrateTest extends IntegrateTest {
  private Member member;
  private HttpHeaders headers;
  private Room testRoom;

  @BeforeEach
  void setup() {
    member = testUtils.createMember();
    memberRepository.save(member);
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @Test
  @DisplayName("Room 삭제 시 200 OK 응답 / 연관된 MemberRoom 삭제")
  public void delete_room_when_room_exists_then_return_200_ok() {
    // given
    // 방 생성
    RoomCreateRequest requestDto =
        RoomCreateRequest.builder().name("삭제 예정 방").description("테스트를 위해 삭제하려는 방 입니다.").build();
    HttpEntity<RoomCreateRequest> requestEntity = new HttpEntity<>(requestDto, headers);
    restTemplate.postForEntity(ROOMS_API_URL, requestEntity, MessageResponse.class);
    // 방 정보 획득
    List<Room> allRooms = roomRepository.findAll();
    testRoom = allRooms.get(0);

    HttpEntity<Void> deleteRequestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<MessageResponse> deleteResponse =
        restTemplate.exchange(
            ROOMS_API_URL + "/" + testRoom.getId(),
            HttpMethod.DELETE,
            deleteRequestEntity,
            MessageResponse.class);

    // then
    // 삭제 응답 상태 검증
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    // memberRoom 삭제 확인
    List<MemberRoom> allMemberRooms = memberRoomRepository.findAll();
    assertThat(allMemberRooms).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("provideInvalidRoomDeleteParameters")
  @DisplayName("Room 삭제시 유효하지 않은 ID인 경우 400 Bad Request 응답")
  void delete_room_with_invalid_id_then_return_400_bad_request(String test, String encodedUrl) {
    // given
    HttpEntity<Void> deleteRequestEntity = new HttpEntity<>(headers);
    String url = ROOMS_API_URL + "/" + encodedUrl;

    // when
    ResponseEntity<ExceptionResponse> response =
        restTemplate.exchange(url, HttpMethod.DELETE, deleteRequestEntity, ExceptionResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ExceptionResponse exceptionResponse = response.getBody();
    assertThat(exceptionResponse).isNotNull();
  }

  private static Stream<Arguments> provideInvalidRoomDeleteParameters() {
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
  @DisplayName("없는 Room ID로 삭제 요청 시 404 Not Found 응답")
  void delete_room_with_nonexistent_id_then_return_404_not_found() {
    // given
    long nonExistentId = 999999L; // 존재하지 않는 ID
    HttpEntity<Void> deleteRequestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<MessageResponse> response =
        restTemplate.exchange(
            ROOMS_API_URL + "/" + nonExistentId,
            HttpMethod.DELETE,
            deleteRequestEntity,
            MessageResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    MessageResponse errorResponse = response.getBody();
    assertThat(errorResponse).isNotNull();
  }
}
