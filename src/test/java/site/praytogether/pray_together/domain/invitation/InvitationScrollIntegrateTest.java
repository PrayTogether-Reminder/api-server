package site.praytogether.pray_together.domain.invitation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.invitation.presentation.dto.InvitationInfoScrollResponse;
import site.praytogether.pray_together.domain.invitation.domain.Invitation;
import site.praytogether.pray_together.domain.invitation.domain.InvitationInfo;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("방 초대 목록 조회 통합 테스트")
public class InvitationScrollIntegrateTest extends IntegrateTest {

  private Member inviteeMember;
  private String token;
  private final int INVITATION_COUNT = 5;

  @BeforeEach
  void setup() throws Exception {
    // 초대 받을 회원 생성
    inviteeMember = testUtils.createUniqueMember();
    memberRepository.save(inviteeMember);
    token = testUtils.createBearerToken(inviteeMember);

    // 초대한 회원들 생성 및 초대장 생성
    for (int i = 0; i < INVITATION_COUNT; i++) {
      // 초대자 생성
      Member inviterMember = testUtils.createUniqueMember();
      memberRepository.save(inviterMember);

      // 방 생성
      Room room = testUtils.createUniqueRoom();
      roomRepository.save(room);

      // 초대장 생성
      Invitation invitation = Invitation.create(inviterMember, inviteeMember, room);
      invitationRepository.save(invitation);
    }
  }

  @Test
  @DisplayName("회원의 초대 목록 조회 시 200 OK 응답 및 초대 목록 확인")
  void fetch_invitation_scroll_then_return_200_ok() throws Exception {
    // given

    // when
    MvcResult result = mockMvc.perform(get(INVITATIONS_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    InvitationInfoScrollResponse response = objectMapper.readValue(responseBody, InvitationInfoScrollResponse.class);
    assertThat(response).as("초대 목록 조회 API 응답 결과가 NULL 입니다.").isNotNull();

    List<InvitationInfo> invitations = response.getInvitations();
    assertThat(invitations).as("초대 목록 조회 API 응답 결과 데이터가 NULL 입니다.").isNotNull();

    assertThat(invitations.size())
        .as("초대 목록 조회 API 응답 결과 개수가 기대값과 다릅니다.")
        .isEqualTo(INVITATION_COUNT);

    assertThat(invitations)
        .as("초대 목록이 createdTime 기준으로 오름차순 정렬되지 않았습니다.")
        .isSortedAccordingTo((i1, i2) -> i1.getCreatedTime().compareTo(i2.getCreatedTime()));

    // 초대 항목 데이터 검증
    for (InvitationInfo invitation : invitations) {
      assertThat(invitation.getInvitationId()).as("초대 ID가 NULL 입니다.").isNotNull();

      assertThat(invitation.getInviterName()).as("초대자 이름이 NULL 입니다.").isNotNull();

      assertThat(invitation.getRoomName()).as("방 이름이 NULL 입니다.").isNotNull();

      assertThat(invitation.getRoomDescription()).as("방 설명이 NULL 입니다.").isNotNull();

      assertThat(invitation.getCreatedTime()).as("초대 생성 시간이 NULL 입니다.").isNotNull();
    }
  }
}
