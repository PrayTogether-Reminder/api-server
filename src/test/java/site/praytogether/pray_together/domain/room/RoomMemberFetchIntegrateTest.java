package site.praytogether.pray_together.domain.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.MemberIdName;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.room.dto.RoomCreateRequest;
import site.praytogether.pray_together.domain.room.dto.RoomMemberResponse;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("방 참가자 조회 통합 테스트")
public class RoomMemberFetchIntegrateTest extends IntegrateTest {

  private Member member;
  private String token;
  private final String MEMBERS_URL = "/members";
  private Room room;
  private int memberCount = 10;

  @BeforeEach
  void setup() throws Exception {
    // 회원 생성 및 JWT 설정
    member = testUtils.createUniqueMember();
    memberRepository.save(member);
    token = testUtils.createBearerToken(member);

    // 방 생성
    RoomCreateRequest createRequest =
        RoomCreateRequest.builder().name("test-name").description("test-description").build();

    mockMvc.perform(post(ROOMS_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated());

    // 방 정보 획득
    List<Room> allRoom = roomRepository.findAll();
    room = allRoom.get(0);

    // 방 참가자 생성 (본인 포함 총 memberCount 명)
    List<MemberRoom> memberRoomList = new ArrayList<>();
    List<Member> memberList = new ArrayList<>();
    for (int i = 0; i < memberCount - 1; i++) {
      Member newMember = testUtils.createUniqueMember();
      memberList.add(newMember);
      MemberRoom memberRoom =
          MemberRoom.builder()
              .member(newMember)
              .room(room)
              .role(RoomRole.MEMBER)
              .isNotification(true)
              .build();
      memberRoomList.add(memberRoom);
    }
    memberRepository.saveAll(memberList);
    memberRoomRepository.saveAll(memberRoomList);
  }

  @Test
  @DisplayName("Room 참가자 조회 시 200 OK 응답")
  void fetch_room_members_then_return_200_ok() throws Exception {
    // when & then
    MvcResult result = mockMvc.perform(get(ROOMS_API_URL + "/" + room.getId() + MEMBERS_URL)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn();

    // 응답 결과 파싱
    String responseBody = result.getResponse().getContentAsString();
    RoomMemberResponse response = objectMapper.readValue(responseBody, RoomMemberResponse.class);

    // 응답 결과 검증
    List<MemberIdName> members = response.getMembers();
    assertThat(members).as("방 참가자 조회 API 응답 결과가 NULL 입니다.").isNotNull();
    assertThat(members.size()).as("방 참가자 조회 API 응답 결과, 방 참가자 수가 예상과 다릅니다.").isEqualTo(memberCount);
    MemberIdName ownerMember =
        MemberIdName.builder().id(member.getId()).name(member.getName()).build();
    assertThat(members.contains(ownerMember)).as("방을 생성한 Member가 요청 방에 포함되지 않고 있습니다.").isTrue();
  }
}