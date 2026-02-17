package site.praytogether.pray_together.domain.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.member_room.model.RoomInfo;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("Room 단일 조회 API 테스트")
class RoomFetchIntegrateTest extends IntegrateTest {

  @Test
  @DisplayName("방 단일 조회 - 방에 속한 회원이면 RoomInfo 응답")
  void get_room_when_member_is_in_room_then_return_room_info() throws Exception {
    // given
    Member owner = testMemberUtils.createSave();
    Member another = testMemberUtils.createSave();
    Room room = testRoomUtils.createSave();

    memberRoomRepository.save(
        MemberRoom.builder()
            .member(owner)
            .room(room)
            .role(RoomRole.OWNER)
            .isNotification(true)
            .build());

    memberRoomRepository.save(
        MemberRoom.builder()
            .member(another)
            .room(room)
            .role(RoomRole.MEMBER)
            .isNotification(false)
            .build());

    String token = testAuthUtils.createBearerToken(owner);

    // when
    MvcResult result =
        mockMvc.perform(
                get(ROOMS_API_URL + "/" + room.getId())
                    .header(HttpHeaders.AUTHORIZATION, token))
            .andExpect(status().isOk())
            .andReturn();

    // then
    RoomInfo response =
        objectMapper.readValue(result.getResponse().getContentAsString(), RoomInfo.class);

    assertThat(response.getId()).isEqualTo(room.getId());
    assertThat(response.getName()).isEqualTo(room.getName());
    assertThat(response.getDescription()).isEqualTo(room.getDescription());
    assertThat(response.getMemberCnt()).isEqualTo(2L);
    assertThat(response.getJoinedTime()).isNotNull();
    assertThat(response.isNotification()).isTrue();
  }

  @Test
  @DisplayName("방 단일 조회 - 방에 속하지 않은 회원이면 404 응답")
  void get_room_when_member_is_not_in_room_then_return_404() throws Exception {
    // given
    Member outsider = testMemberUtils.createSave();
    Member owner = testMemberUtils.createSave();
    Room room = testRoomUtils.createSave();

    memberRoomRepository.save(
        MemberRoom.builder()
            .member(owner)
            .room(room)
            .role(RoomRole.OWNER)
            .isNotification(true)
            .build());

    String token = testAuthUtils.createBearerToken(outsider);

    // when & then
    mockMvc.perform(get(ROOMS_API_URL + "/" + room.getId())
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNotFound());
  }
}
