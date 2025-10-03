package site.praytogether.pray_together.domain.room;

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
import site.praytogether.pray_together.domain.room.dto.RoomCreateRequest;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("Room 생성 통합 테스트")
public class RoomCreateIntegrateTest extends IntegrateTest {
  private Member member;
  private String token;

  @BeforeEach
  void setup() {
    member = testUtils.createUniqueMember();
    memberRepository.save(member);
    token = testUtils.createBearerToken(member);
  }

  @Test
  @DisplayName("Room 생성 시 201 Created 응답")
  public void create_room_with_valid_input_then_return_201_created() throws Exception {
    // given - Request Body 준비
    RoomCreateRequest requestDto =
        RoomCreateRequest.builder().name("테스트 방").description("테스트를 위한 방입니다.").build();

    // when & then
    mockMvc.perform(post(ROOMS_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").exists());

    // DB 검증
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

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("provideInvalidRoomCreateParameters")
  @DisplayName("Room 생성 시 유효하지 않은 파라미터인 경우 400 Bad Request 응답")
  void create_room_with_invalid_input_then_return_400_bad_request(
      String test, String name, String description) throws Exception {

    // given
    RoomCreateRequest requestDto =
        RoomCreateRequest.builder().name(name).description(description).build();

    // when & then
    mockMvc.perform(post(ROOMS_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isBadRequest());

    // DB 검증 - 방이 생성되지 않았는지 확인
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
