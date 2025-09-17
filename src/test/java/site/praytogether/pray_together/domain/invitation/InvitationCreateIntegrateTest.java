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
import site.praytogether.pray_together.domain.invitation.dto.InvitationCreateRequest;
import site.praytogether.pray_together.domain.invitation.model.Invitation;
import site.praytogether.pray_together.domain.invitation.model.InvitationStatus;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("방 초대 통합 테스트")
public class InvitationCreateIntegrateTest extends IntegrateTest {
  private Member memberInviter;
  private Member memberInvitee;
  private Room room;
  private String token;

  @BeforeEach
  void setup() throws Exception {
    memberInviter = testUtils.createUniqueMember();
    memberRepository.save(memberInviter);
    memberInvitee = testUtils.createUniqueMember();
    memberRepository.save(memberInvitee);

    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    MemberRoom memberRoom =
        testUtils.createUniqueMemberRoom_With_Member_AND_Room(memberInviter, room);
    memberRoomRepository.save(memberRoom);
  }

  @Test
  @DisplayName("방 초대시 201 Created 응답")
  void invite_member_to_room_then_return_201_created() throws Exception {
    // given
    token = testUtils.createBearerToken(memberInviter);
    InvitationCreateRequest request =
        InvitationCreateRequest.builder()
            .roomId(room.getId())
            .email(memberInvitee.getEmail())
            .build();

    // when
    mockMvc.perform(post(INVITATIONS_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    // then

    List<Invitation> allInvitation = invitationRepository.findAll();
    assertThat(allInvitation.size()).as("저장된 초대장의 개수가 예상과 다릅니다.").isOne();

    Invitation invitation = allInvitation.get(0);
    assertThat(invitation.getInviterName())
        .as("초대자 이름이 예상과 다릅니다.")
        .isEqualTo(memberInviter.getName());

    assertThat(invitation.getInvitee().getId())
        .as("초대받은 사용자 ID가 예상과 다릅니다.")
        .isEqualTo(memberInvitee.getId());

    assertThat(invitation.getRoom().getId()).as("초대장에 연결된 방 ID가 예상과 다릅니다.").isEqualTo(room.getId());

    assertThat(invitation.getResponseTime()).as("초대장 응답 시간은 null이어야 합니다.").isNull();

    assertThat(invitation.getStatus())
        .as("초대장 상태가 PENDING이 아닙니다.")
        .isEqualTo(InvitationStatus.PENDING);
  }
}
