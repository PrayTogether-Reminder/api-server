package site.praytogether.pray_together.domain.invitation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.invitation.presentation.v2.dto.InvitationCreateRequestV2;
import site.praytogether.pray_together.domain.invitation.domain.Invitation;
import site.praytogether.pray_together.domain.invitation.domain.InvitationStatus;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.exception.spec.MemberRoomExceptionSpec;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("방 초대 V2 통합 테스트 - 여러 명 초대")
public class InvitationCreateIntegrateTestV2 extends IntegrateTest {
  private Member memberInviter;
  private Member friend1;
  private Member friend2;
  private Member friend3;
  private Room room;
  private String token;
  private String ROOM_INVITATION_URL = "/invitations";

  @BeforeEach
  void setup() throws Exception {
    memberInviter = testUtils.createUniqueMember();
    memberRepository.save(memberInviter);

    friend1 = testUtils.createUniqueMember();
    friend2 = testUtils.createUniqueMember();
    friend3 = testUtils.createUniqueMember();
    memberRepository.saveAll(List.of(friend1, friend2, friend3));

    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    MemberRoom memberRoom =
        testUtils.createUniqueMemberRoom_With_Member_AND_Room(memberInviter, room);
    memberRoomRepository.save(memberRoom);

    token = testUtils.createBearerToken(memberInviter);
  }

  @Test
  @DisplayName("여러 친구를 방에 초대하면 201 Created 응답 및 모든 초대장 생성")
  void invite_multiple_friends_to_room_then_return_201_and_create_all_invitations() throws Exception {
    // given
    List<Long> friends = List.of(friend1.getId(), friend2.getId(), friend3.getId());
    InvitationCreateRequestV2 request = new InvitationCreateRequestV2(room.getId(),friends);

    // when
    mockMvc.perform(post(API_VERSION_2 + ROOM_INVITATION_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    // then
    List<Invitation> allInvitations = invitationRepository.findAll();
    assertThat(allInvitations).as("저장된 초대장의 개수가 예상과 다릅니다.").hasSize(friends.size());

    assertThat(allInvitations)
        .as("모든 초대장의 초대자가 동일해야 합니다.")
        .allMatch(invitation -> invitation.getInviterName().equals(memberInviter.getName()));

    assertThat(allInvitations)
        .as("초대받은 사용자들이 예상과 다릅니다.")
        .extracting(invitation -> invitation.getInvitee().getId())
        .containsExactlyInAnyOrder(friend1.getId(), friend2.getId(), friend3.getId());

    assertThat(allInvitations)
        .as("모든 초대장이 동일한 방에 대한 것이어야 합니다.")
        .allMatch(invitation -> invitation.getRoom().getId().equals(room.getId()));

    assertThat(allInvitations)
        .as("모든 초대장의 상태가 PENDING이어야 합니다.")
        .allMatch(invitation -> invitation.getStatus() == InvitationStatus.PENDING);

    assertThat(allInvitations)
        .as("모든 초대장의 응답 시간은 null이어야 합니다.")
        .allMatch(invitation -> invitation.getResponseTime() == null);
  }

  @Test
  @DisplayName("초대할 친구 중 한 명이라도 이미 방에 있으면 400 Bad Request 응답")
  void invite_friends_when_one_already_in_room_then_return_400() throws Exception {
    // given
    MemberRoom friend1InRoom =
        testUtils.createUniqueMemberRoom_With_Member_AND_Room(friend1, room);
    memberRoomRepository.save(friend1InRoom);

    InvitationCreateRequestV2 request = new InvitationCreateRequestV2(
        room.getId(),
        List.of(friend1.getId(), friend2.getId(), friend3.getId())
    );

    // when & then
    mockMvc.perform(post(API_VERSION_2 + ROOM_INVITATION_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(MemberRoomExceptionSpec.SOMONE_ALREADY_EXIST.getCode()));

    List<Invitation> allInvitations = invitationRepository.findAll();
    assertThat(allInvitations).as("초대장이 생성되지 않아야 합니다.").isEmpty();
  }

  @Test
  @DisplayName("빈 친구 리스트로 초대 시도하면 400 Bad Request 응답")
  void invite_with_empty_friend_list_then_return_400() throws Exception {
    // given
    InvitationCreateRequestV2 request = new InvitationCreateRequestV2(
        room.getId(),
        List.of()
    );

    // when & then
    mockMvc.perform(post(API_VERSION_2 + ROOM_INVITATION_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("음수 친구 ID로 초대 시도하면 400 Bad Request 응답")
  void invite_with_negative_friend_id_then_return_400() throws Exception {
    // given
    InvitationCreateRequestV2 request = new InvitationCreateRequestV2(
        room.getId(),
        List.of(-1L, friend2.getId())
    );

    // when & then
    mockMvc.perform(post(API_VERSION_2 + ROOM_INVITATION_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("0 친구 ID로 초대 시도하면 400 Bad Request 응답")
  void invite_with_zero_friend_id_then_return_400() throws Exception {
    // given
    InvitationCreateRequestV2 request = new InvitationCreateRequestV2(
        room.getId(),
        List.of(0L, friend2.getId())
    );

    // when & then
    mockMvc.perform(post(API_VERSION_2 + ROOM_INVITATION_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("이미 PENDING 초대장이 있는 친구는 제외하고 나머지만 초대")
  void invite_friends_excluding_already_pending_invitations() throws Exception {
    // given
    Invitation existingInvitation = Invitation.create(memberInviter, friend1, room);
    invitationRepository.save(existingInvitation);

    InvitationCreateRequestV2 request = new InvitationCreateRequestV2(
        room.getId(),
        List.of(friend1.getId(), friend2.getId(), friend3.getId())
    );

    // when
    mockMvc.perform(post(API_VERSION_2 + ROOM_INVITATION_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    // then
    List<Invitation> allInvitations = invitationRepository.findAll();
    assertThat(allInvitations).as("총 초대장 개수: 기존 1개 + 새로 생성된 2개").hasSize(3);

    List<Invitation> newInvitations = allInvitations.stream()
        .filter(inv -> !inv.getId().equals(existingInvitation.getId()))
        .toList();

    assertThat(newInvitations).as("새로 생성된 초대장은 2개여야 합니다.").hasSize(2);

    assertThat(newInvitations)
        .as("friend1은 제외되고 friend2, friend3만 초대장이 생성되어야 합니다.")
        .extracting(invitation -> invitation.getInvitee().getId())
        .containsExactlyInAnyOrder(friend2.getId(), friend3.getId());

    assertThat(newInvitations)
        .as("새로운 초대장의 상태가 PENDING이어야 합니다.")
        .allMatch(invitation -> invitation.getStatus() == InvitationStatus.PENDING);
  }
}
