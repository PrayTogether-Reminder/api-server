package site.praytogether.pray_together.domain.room;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.web.util.UriComponentsBuilder;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.member_room.model.RoomInfo;
import site.praytogether.pray_together.domain.room.dto.RoomScrollResponse;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("Room 무한 스크롤 테스트")
public class RoomInfiniteScrollIntegrateTest extends IntegrateTest {

  private Member member;
  private HttpHeaders headers;

  private final String ORDER_BY = "orderBy";
  private final String AFTER = "after";
  private final String DIR = "dir";

  private final String ORDER_BY_TIME = "time";
  private final String DIR_DESC = "dsec";

  @BeforeEach
  void setup() {
    // member1 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);
    // room1 생성

    for (int i = 0; i < 30; i++) {
      Room testRoom = Room.create("test" + (i + 1), "test-description" + (i + 1));
      roomRepository.save(testRoom);
    }

    List<Room> allRoom = roomRepository.findAll();

    // member1는 홀수 ID 방 추가
    for (int i = 0; i < 30; i++) {
      Room room = allRoom.get(i);
      if (room.getId() % 2 == 0) continue;
      MemberRoom memberRoom =
          MemberRoom.builder()
              .member(member)
              .room(room)
              .role(RoomRole.MEMBER)
              .isNotification(true)
              .build();
      memberRoomRepository.save(memberRoom);
    }

    // member2 생성
    Member member2 = testUtils.createUniqueMember();
    memberRepository.save(member2);
    // member2는 짝수 ID 방 추가
    for (int i = 0; i < 30; i++) {
      Room room = allRoom.get(i);
      if (room.getId() % 2 == 1) continue;
      MemberRoom memberRoom =
          MemberRoom.builder()
              .member(member2)
              .room(room)
              .role(RoomRole.MEMBER)
              .isNotification(true)
              .build();
      memberRoomRepository.save(memberRoom);
    }
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("provideRoomScrollParameters")
  @DisplayName("다양한 파라미터 조합 요청시 기본값으로 정상 처리되어 200 OK 응답")
  void fetch_rooms_list_with_default_values_for_different_params_then_return_200_ok(
      String test, String orderBy, String after, String dir) {

    // given
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
    String uri =
        UriComponentsBuilder.fromUriString(ROOMS_API_URL)
            .queryParam(ORDER_BY, orderBy)
            .queryParam(AFTER, after)
            .queryParam(DIR, dir)
            .build()
            .toUriString();
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<RoomScrollResponse> responseEntity =
        restTemplate.exchange(uri, HttpMethod.GET, requestEntity, RoomScrollResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("방 목록 무한 스크롤 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);
    RoomScrollResponse response = responseEntity.getBody();
    assertThat(response).as("방 목록 무한 스크롤 API 응답 결과가 NULL 입니다.").isNotNull();
    List<RoomInfo> rooms = response.getRooms();
    assertThat(rooms.size()).as("방 목록 무한 스크롤 API 응답 결과 데이터가 없습니다.").isGreaterThan(0);

    assertThat(rooms)
        .as("방 목록이 joinedTime 기준으로 내림차순 정렬되지 않았습니다.")
        .isSortedAccordingTo(
            (room1, room2) -> room2.getJoinedTime().compareTo(room1.getJoinedTime()));

    assertThat(rooms).as("모든 방의 ID가 홀수여야 합니다.").allMatch(room -> room.getRoomId() % 2 == 1);
  }

  private static Stream<Arguments> provideRoomScrollParameters() {
    return Stream.of(
        // 기본값 테스트
        Arguments.of("기본값", "time", "0", "desc"),

        // orderBy 파라미터 변형
        Arguments.of("orderBy null", null, "0", "desc"),
        Arguments.of("orderBy 빈값", "", "0", "desc"),

        // after 파라미터 변형
        Arguments.of("after null", "time", null, "desc"),
        Arguments.of("after 빈값", "time", "", "desc"),

        // dir 파라미터 변형
        Arguments.of("dir null", "time", "0", null),
        Arguments.of("dir 빈값", "time", "0", ""),

        // 여러 파라미터 조합
        Arguments.of("모든 값 null", null, null, null), // 모든 파라미터 null
        Arguments.of("모든 값 빈값", "", "", "") // 모든 파라미터 빈 문자열
        );
  }

  @Test
  @DisplayName("time 기준 desc 정렬로 연속 요청시 데이터가 정상적으로 페이징되고 마지막에는 빈 컬렉션 응답")
  void fetch_rooms_list_with_sequential_requests_time_desc_and_empty_final_response() {

    // given
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
    String uri =
        UriComponentsBuilder.fromUriString(ROOMS_API_URL)
            .queryParam(ORDER_BY, ORDER_BY_TIME)
            .queryParam(AFTER, "0")
            .queryParam(DIR, DIR_DESC)
            .build()
            .toUriString();
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // 첫 번째 요청
    ResponseEntity<RoomScrollResponse> responseEntity =
        restTemplate.exchange(uri, HttpMethod.GET, requestEntity, RoomScrollResponse.class);

    assertThat(responseEntity.getStatusCode())
        .as("첫 번째 요청: 방 목록 무한 스크롤 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    List<RoomInfo> rooms = responseEntity.getBody().getRooms();

    // 모든 방을 가져올 때까지 반복 요청
    while (!rooms.isEmpty()) {
      // 현재 응답 검증
      assertThat(rooms)
          .as("방 목록이 joinedTime 기준으로 내림차순 정렬되지 않았습니다.")
          .isSortedAccordingTo(
              (room1, room2) -> room2.getJoinedTime().compareTo(room1.getJoinedTime()));

      assertThat(rooms).as("모든 방의 ID가 홀수여야 합니다.").allMatch(room -> room.getRoomId() % 2 == 1);

      // 다음 요청 준비
      RoomInfo lastRoom = rooms.get(rooms.size() - 1);
      uri =
          UriComponentsBuilder.fromUriString(ROOMS_API_URL)
              .queryParam(ORDER_BY, ORDER_BY_TIME)
              .queryParam(AFTER, String.valueOf(lastRoom.getJoinedTime()))
              .queryParam(DIR, DIR_DESC)
              .build()
              .toUriString();

      // 다음 요청 수행
      responseEntity =
          restTemplate.exchange(uri, HttpMethod.GET, requestEntity, RoomScrollResponse.class);

      assertThat(responseEntity.getStatusCode())
          .as("연속 요청: 방 목록 무한 스크롤 API 응답 상태 코드가 200 OK가 아닙니다.")
          .isEqualTo(HttpStatus.OK);

      assertThat(responseEntity.getBody())
          .as("연속 요청: 방 목록 무한 스크롤 API 응답 결과가 NULL 입니다.")
          .isNotNull();

      rooms = responseEntity.getBody().getRooms();
    }

    assertThat(rooms).as("마지막 응답: 빈 컬렉션이어야 합니다.").isEmpty();
  }
}
