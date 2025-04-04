package site.praytogether.pray_together.domain.room;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.member_room.repository.MemberRoomRepository;
import site.praytogether.pray_together.domain.room.dto.RoomCreateRequest;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.repository.RoomRepository;
import site.praytogether.pray_together.exception.ExceptionResponse;
import site.praytogether.pray_together.test_config.TestUtils;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoomIntegrateTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private RoomRepository roomRepository;
  @Autowired private MemberRepository memberRepository;
  @Autowired private MemberRoomRepository memberRoomRepository;
  @Autowired private TestUtils testUtils;

  private final String ROOMS_API_URL = "/api/v1/rooms";
  private Member member;
  private HttpHeaders headers;

  @BeforeEach
  void setup() {
    member = testUtils.createMember();
    memberRepository.save(member);
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
  }

  @AfterEach
  void cleanup() {
    memberRepository.deleteAll();
    memberRoomRepository.deleteAll();
    roomRepository.deleteAll();
  }

  @Nested
  @DisplayName("Room 생성 테스트")
  class RoomCreateTest {

    @Test
    @DisplayName("Room 생성 시 201 Created 응답")
    public void create_room_with_valid_input_then_return_201_created() {
      // given - Request Body 준비
      RoomCreateRequest requestDto =
          RoomCreateRequest.builder().name("테스트 방").description("테스트를 위한 방입니다.").build();
      HttpEntity<RoomCreateRequest> requestEntity = new HttpEntity<>(requestDto, headers);

      // when - API 요청
      ResponseEntity<MessageResponse> response =
          restTemplate.postForEntity(ROOMS_API_URL, requestEntity, MessageResponse.class);

      // then
      // API 응답 검증
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

      // 생성된 Room 확인
      List<Room> allRooms = roomRepository.findAll();
      assertThat(allRooms).isNotEmpty();

      Room createdRoom = allRooms.get(0);
      assertThat(createdRoom.getName()).isEqualTo("테스트 방");
      assertThat(createdRoom.getDescription()).isEqualTo("테스트를 위한 방입니다.");

      // 생성된 Member-Room 확인
      List<MemberRoom> memberRooms = memberRoomRepository.findAll();
      assertThat(memberRooms).isNotEmpty();
      assertThat(memberRooms.get(0).getMember().getId()).isEqualTo(member.getId());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRoomCreateParameters")
    @DisplayName("Room 생성 시 유효하지 않은 파라미터인 경우 400 Bad Request 응답")
    void create_room_with_invalid_input_then_return_400_bad_request(
        String test, String name, String description) {

      // given
      RoomCreateRequest requestDto =
          RoomCreateRequest.builder().name(name).description(description).build();

      HttpEntity<RoomCreateRequest> requestEntity = new HttpEntity<>(requestDto, headers);

      // when
      ResponseEntity<MessageResponse> response =
          restTemplate.postForEntity(ROOMS_API_URL, requestEntity, MessageResponse.class);

      // then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      MessageResponse errorResponse = response.getBody();
      assertThat(errorResponse).isNotNull();

      // 방이 생성되지 않았는지 확인
      assertThat(roomRepository.findAll()).isEmpty();
    }

    private static Stream<Arguments> provideInvalidRoomCreateParameters() {
      return Stream.of(
          Arguments.of("방 이름 null", null, "정상적인 방 설명입니다."),
          Arguments.of("방 이름 빈 문자열", "", "정상적인 방 설명입니다."),
          Arguments.of("방 이름 공백만 포함", "   ", "정상적인 방 설명입니다."),
          Arguments.of("방 이름 최대 길이 초과(51자)", "a".repeat(51), "정상적인 방 설명입니다."),
          Arguments.of("방 설명 null", "정상적인 방 이름", null),
          Arguments.of("방 설명 빈 문자열", "정상적인 방 이름", ""),
          Arguments.of("방 설명 최대 길이 초과(201자)", "정상적인 방 이름", "a".repeat(201)));
    }
  }

  @Nested
  @DisplayName("Room 삭제 테스트")
  class RoomDeleteTest {

    @Test
    @DisplayName("Room 삭제 시 200 OK 응답 / 연관된 MemberRoom  삭제")
    public void delete_room_when_room_exists_then_return_200_ok() {
      // given
      RoomCreateRequest requestDto =
          RoomCreateRequest.builder().name("삭제 예정 방").description("테스트를 위해 삭제하려는 방 입니다.").build();

      HttpEntity<RoomCreateRequest> requestEntity = new HttpEntity<>(requestDto, headers);

      ResponseEntity<MessageResponse> createResponse =
          restTemplate.postForEntity(ROOMS_API_URL, requestEntity, MessageResponse.class);

      assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

      List<Room> allRooms = roomRepository.findAll();
      Room room = allRooms.get(0);

      // when
      HttpEntity<Void> deleteRequestEntity = new HttpEntity<>(headers);
      ResponseEntity<MessageResponse> deleteResponse =
          restTemplate.exchange(
              ROOMS_API_URL + "/" + room.getId(),
              HttpMethod.DELETE,
              deleteRequestEntity,
              MessageResponse.class);

      // then
      // 삭제 응답 검증
      assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

      // memberRoom 삭제 검증
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
          restTemplate.exchange(
              url, HttpMethod.DELETE, deleteRequestEntity, ExceptionResponse.class);

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
    @DisplayName("존재하지 않는 Room ID로 삭제 요청 시 404 Not Found 응답")
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

    @Test
    @DisplayName("다른 사용자의 Room ID로 삭제 요청 시 403 Forbidden 응답")
    void delete_room_owned_by_other_user_then_return_403_forbidden() {
      // given
      // 새로운 사용자 생성
      Member otherMember = testUtils.createMember();
      memberRepository.save(otherMember);

      // 다른 사용자로 방 생성
      HttpHeaders otherUserHeaders = testUtils.create_Auth_HttpHeader_With_Member(otherMember);
      RoomCreateRequest requestDto =
          RoomCreateRequest.builder().name("다른 사용자의 방").description("다른 사용자가 생성한 방입니다.").build();

      HttpEntity<RoomCreateRequest> createRequestEntity =
          new HttpEntity<>(requestDto, otherUserHeaders);
      restTemplate.postForEntity(ROOMS_API_URL, createRequestEntity, MessageResponse.class);

      // 생성된 방 조회
      List<Room> allRooms = roomRepository.findAll();
      Room otherUserRoom =
          allRooms.stream()
              .filter(room -> room.getName().equals("다른 사용자의 방"))
              .findFirst()
              .orElseThrow();

      // when - 원래 사용자의 헤더로 다른 사용자의 방 삭제 시도
      HttpEntity<Void> deleteRequestEntity = new HttpEntity<>(headers);
      ResponseEntity<MessageResponse> response =
          restTemplate.exchange(
              ROOMS_API_URL + "/" + otherUserRoom.getId(),
              HttpMethod.DELETE,
              deleteRequestEntity,
              MessageResponse.class);

      // then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
      MessageResponse errorResponse = response.getBody();
      assertThat(errorResponse).isNotNull();

      // 방이 여전히 존재하는지 확인
      assertThat(roomRepository.findById(otherUserRoom.getId())).isPresent();
    }
  }
}
