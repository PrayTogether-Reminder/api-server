package site.praytogether.pray_together.domain.friend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationStatus;
import site.praytogether.pray_together.domain.friend.presentation.dto.FetchReceivedInvitationResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("받은 친구 초대 목록 조회 통합 테스트")
public class FriendReceivedInvitationsFetchIntegrateTest extends IntegrateTest {
  private Member receiver;
  private Member sender1;
  private Member sender2;
  private Member sender3;
  private String receiverToken;

  @BeforeEach
  void setup() throws Exception {
    receiver = testUtils.createUniqueMember();
    memberRepository.save(receiver);

    sender1 = testUtils.createUniqueMember();
    memberRepository.save(sender1);

    sender2 = testUtils.createUniqueMember();
    memberRepository.save(sender2);

    sender3 = testUtils.createUniqueMember();
    memberRepository.save(sender3);

    receiverToken = testUtils.createBearerToken(receiver);
  }

  @Test
  @DisplayName("받은 초대가 없을 때 빈 목록 반환")
  void fetch_received_invitations_when_none_then_return_empty_list() throws Exception {
    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL + "/requests")
            .header(HttpHeaders.AUTHORIZATION, receiverToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchReceivedInvitationResponse response = objectMapper.readValue(content, FetchReceivedInvitationResponse.class);

    assertThat(response.friendInvitations())
        .as("받은 초대가 없어야 합니다")
        .isEmpty();
  }

  @Test
  @DisplayName("PENDING 상태의 받은 초대 목록 조회 성공")
  void fetch_pending_received_invitations_then_return_list() throws Exception {
    // given
    FriendInvitation invitation1 = FriendInvitation.builder()
        .sender(sender1)
        .receiver(receiver)
        .status(FriendInvitationStatus.PENDING)
        .build();
    friendInvitationRepository.save(invitation1);

    FriendInvitation invitation2 = FriendInvitation.builder()
        .sender(sender2)
        .receiver(receiver)
        .status(FriendInvitationStatus.PENDING)
        .build();
    friendInvitationRepository.save(invitation2);

    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL + "/requests")
            .header(HttpHeaders.AUTHORIZATION, receiverToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchReceivedInvitationResponse response = objectMapper.readValue(content, FetchReceivedInvitationResponse.class);

    assertThat(response.friendInvitations())
        .as("2개의 초대가 조회되어야 합니다")
        .hasSize(2);

    assertThat(response.friendInvitations())
        .extracting("invitationId")
        .as("초대 ID가 올바르게 조회되어야 합니다")
        .containsExactly(invitation1.getId(), invitation2.getId());

    assertThat(response.friendInvitations())
        .extracting("senderName")
        .as("발신자 이름이 올바르게 조회되어야 합니다")
        .containsExactly(sender1.getName(), sender2.getName());
  }

  @Test
  @DisplayName("ACCEPTED나 REJECTED 상태의 초대는 조회되지 않음")
  void fetch_invitations_excludes_non_pending_status() throws Exception {
    // given
    // PENDING 초대
    FriendInvitation pendingInvitation = FriendInvitation.builder()
        .sender(sender1)
        .receiver(receiver)
        .status(FriendInvitationStatus.PENDING)
        .build();
    friendInvitationRepository.save(pendingInvitation);

    // ACCEPTED 초대 (조회되면 안됨)
    FriendInvitation acceptedInvitation = FriendInvitation.builder()
        .sender(sender2)
        .receiver(receiver)
        .status(FriendInvitationStatus.ACCEPTED)
        .build();
    friendInvitationRepository.save(acceptedInvitation);

    // REJECTED 초대 (조회되면 안됨)
    FriendInvitation rejectedInvitation = FriendInvitation.builder()
        .sender(sender3)
        .receiver(receiver)
        .status(FriendInvitationStatus.REJECTED)
        .build();
    friendInvitationRepository.save(rejectedInvitation);

    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL + "/requests")
            .header(HttpHeaders.AUTHORIZATION, receiverToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchReceivedInvitationResponse response = objectMapper.readValue(content, FetchReceivedInvitationResponse.class);

    assertThat(response.friendInvitations())
        .as("PENDING 초대만 조회되어야 합니다")
        .hasSize(1);

    assertThat(response.friendInvitations().get(0).invitationId())
        .as("초대 ID가 PENDING 초대의 ID와 같아야 합니다")
        .isEqualTo(pendingInvitation.getId());

    assertThat(response.friendInvitations().get(0).senderName())
        .as("발신자 이름이 올바르게 조회되어야 합니다")
        .isEqualTo(sender1.getName());
  }

  @Test
  @DisplayName("내가 보낸 초대는 조회되지 않음")
  void fetch_invitations_excludes_sent_invitations() throws Exception {
    // given
    // 내가 받은 초대
    FriendInvitation receivedInvitation = FriendInvitation.builder()
        .sender(sender1)
        .receiver(receiver)
        .status(FriendInvitationStatus.PENDING)
        .build();
    friendInvitationRepository.save(receivedInvitation);

    // 내가 보낸 초대 (조회되면 안됨)
    FriendInvitation sentInvitation = FriendInvitation.builder()
        .sender(receiver)
        .receiver(sender2)
        .status(FriendInvitationStatus.PENDING)
        .build();
    friendInvitationRepository.save(sentInvitation);

    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL + "/requests")
            .header(HttpHeaders.AUTHORIZATION, receiverToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchReceivedInvitationResponse response = objectMapper.readValue(content, FetchReceivedInvitationResponse.class);

    assertThat(response.friendInvitations())
        .as("받은 초대만 조회되어야 합니다")
        .hasSize(1);

    assertThat(response.friendInvitations().get(0).invitationId())
        .as("받은 초대의 ID와 같아야 합니다")
        .isEqualTo(receivedInvitation.getId());

    assertThat(response.friendInvitations().get(0).senderName())
        .as("발신자 이름이 sender1이어야 합니다")
        .isEqualTo(sender1.getName());
  }

  @Test
  @DisplayName("여러 명으로부터 받은 초대 목록 조회")
  void fetch_multiple_received_invitations() throws Exception {
    // given - 3명으로부터 초대 받음
    FriendInvitation invitation1 = FriendInvitation.builder()
        .sender(sender1)
        .receiver(receiver)
        .status(FriendInvitationStatus.PENDING)
        .build();
    friendInvitationRepository.save(invitation1);

    FriendInvitation invitation2 = FriendInvitation.builder()
        .sender(sender2)
        .receiver(receiver)
        .status(FriendInvitationStatus.PENDING)
        .build();
    friendInvitationRepository.save(invitation2);

    FriendInvitation invitation3 = FriendInvitation.builder()
        .sender(sender3)
        .receiver(receiver)
        .status(FriendInvitationStatus.PENDING)
        .build();
    friendInvitationRepository.save(invitation3);

    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL + "/requests")
            .header(HttpHeaders.AUTHORIZATION, receiverToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchReceivedInvitationResponse response = objectMapper.readValue(content, FetchReceivedInvitationResponse.class);

    assertThat(response.friendInvitations())
        .as("3개의 초대가 모두 조회되어야 합니다")
        .hasSize(3);

    assertThat(response.friendInvitations())
        .extracting("invitationId")
        .as("모든 초대 ID가 포함되어야 합니다")
        .containsExactlyInAnyOrder(invitation1.getId(), invitation2.getId(), invitation3.getId());

    assertThat(response.friendInvitations())
        .extracting("senderName")
        .as("모든 발신자 이름이 포함되어야 합니다")
        .containsExactlyInAnyOrder(sender1.getName(), sender2.getName(), sender3.getName());
  }
}