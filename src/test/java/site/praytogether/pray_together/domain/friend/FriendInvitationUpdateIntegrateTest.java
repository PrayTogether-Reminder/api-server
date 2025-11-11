package site.praytogether.pray_together.domain.friend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static site.praytogether.pray_together.domain.friend.domain.exception.FriendExceptionSpec.FRIEND_INVITATION_NOF_FOUND;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.friend.domain.exception.FriendExceptionSpec;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationStatus;
import site.praytogether.pray_together.domain.friend.presentation.dto.UpdateReceivedInvitationRequest;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("친구 초대 응답 통합 테스트")
public class FriendInvitationUpdateIntegrateTest extends IntegrateTest {
  private Member sender;
  private Member receiver;
  private Member otherMember;
  private String receiverToken;
  private String otherMemberToken;

  @BeforeEach
  void setup() throws Exception {
    sender = testUtils.createUniqueMember();
    memberRepository.save(sender);

    receiver = testUtils.createUniqueMember();
    memberRepository.save(receiver);

    otherMember = testUtils.createUniqueMember();
    memberRepository.save(otherMember);

    receiverToken = testUtils.createBearerToken(receiver);
    otherMemberToken = testUtils.createBearerToken(otherMember);
  }

  @Test
  @DisplayName("친구 초대 수락 성공")
  void accept_friend_invitation_then_return_200() throws Exception {
    // given
    FriendInvitation invitation = FriendInvitation.create(sender, receiver);
    friendInvitationRepository.save(invitation);

    UpdateReceivedInvitationRequest request = new UpdateReceivedInvitationRequest(FriendInvitationStatus.ACCEPTED);

    // when
    MvcResult result = mockMvc.perform(patch(FRIEND_API_URL + "/requests/{invitationId}", invitation.getId())
            .header(HttpHeaders.AUTHORIZATION, receiverToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    // then - 응답 검증
    String content = result.getResponse().getContentAsString();
    MessageResponse response = objectMapper.readValue(content, MessageResponse.class);
    assertThat(response.getMessage())
        .as("수락 메시지가 반환되어야 합니다")
        .isEqualTo("친구 요청을 수락했습니다.");

    // then - 초대 상태 검증
    Optional<FriendInvitation> updatedInvitation = friendInvitationRepository
        .findByReceiver_IdAndId(receiver.getId(), invitation.getId());

    assertThat(updatedInvitation)
        .as("초대가 존재해야 합니다")
        .isPresent();

    assertThat(updatedInvitation.get().getStatus())
        .as("초대 상태가 ACCEPTED여야 합니다")
        .isEqualTo(FriendInvitationStatus.ACCEPTED);

    // then - 친구 관계 생성 검증
    boolean isFriend = friendshipRepository.isExist(sender.getId(), receiver.getId())
        || friendshipRepository.isExist(receiver.getId(), sender.getId());

    assertThat(isFriend)
        .as("친구 관계가 생성되어야 합니다")
        .isTrue();
  }

  @Test
  @DisplayName("친구 초대 거절 성공")
  void reject_friend_invitation_then_return_200() throws Exception {
    // given
    FriendInvitation invitation = FriendInvitation.create(sender, receiver);
    friendInvitationRepository.save(invitation);

    UpdateReceivedInvitationRequest request = new UpdateReceivedInvitationRequest(FriendInvitationStatus.REJECTED);

    // when
    MvcResult result = mockMvc.perform(patch(FRIEND_API_URL + "/requests/{invitationId}", invitation.getId())
            .header(HttpHeaders.AUTHORIZATION, receiverToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andReturn();

    // then - 응답 검증
    String content = result.getResponse().getContentAsString();
    MessageResponse response = objectMapper.readValue(content, MessageResponse.class);
    assertThat(response.getMessage())
        .as("거절 메시지가 반환되어야 합니다")
        .isEqualTo("친구 요청을 거절했습니다.");

    // then - 초대 상태 검증
    Optional<FriendInvitation> updatedInvitation = friendInvitationRepository
        .findByReceiver_IdAndId(receiver.getId(), invitation.getId());

    assertThat(updatedInvitation)
        .as("초대가 존재해야 합니다")
        .isPresent();

    assertThat(updatedInvitation.get().getStatus())
        .as("초대 상태가 REJECTED여야 합니다")
        .isEqualTo(FriendInvitationStatus.REJECTED);

    // then - 친구 관계 미생성 검증
    boolean isFriend = friendshipRepository.isExist(sender.getId(), receiver.getId())
        || friendshipRepository.isExist(receiver.getId(), sender.getId());

    assertThat(isFriend)
        .as("친구 관계가 생성되지 않아야 합니다")
        .isFalse();
  }

  @Test
  @DisplayName("존재하지 않는 초대에 응답시 404 응답")
  void update_non_existent_invitation_then_return_404() throws Exception {
    // given
    Long nonExistentInvitationId = 99999L;
    UpdateReceivedInvitationRequest request = new UpdateReceivedInvitationRequest(FriendInvitationStatus.ACCEPTED);

    // when & then
    mockMvc.perform(patch(FRIEND_API_URL + "/requests/{invitationId}", nonExistentInvitationId)
            .header(HttpHeaders.AUTHORIZATION, receiverToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(FRIEND_INVITATION_NOF_FOUND.getCode()));
  }

  @Test
  @DisplayName("다른 사용자의 초대를 응답하려 할 때 404 응답")
  void update_other_user_invitation_then_return_404() throws Exception {
    // given - sender가 otherMember에게 보낸 초대
    FriendInvitation invitation = FriendInvitation.create(sender, otherMember);
    friendInvitationRepository.save(invitation);

    UpdateReceivedInvitationRequest request = new UpdateReceivedInvitationRequest(FriendInvitationStatus.ACCEPTED);

    // when & then - receiver가 응답 시도
    mockMvc.perform(patch(FRIEND_API_URL + "/requests/{invitationId}", invitation.getId())
            .header(HttpHeaders.AUTHORIZATION, receiverToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(FRIEND_INVITATION_NOF_FOUND.getCode()));

    // then - 초대 상태는 변경되지 않아야 함
    Optional<FriendInvitation> unchangedInvitation = friendInvitationRepository
        .findByReceiver_IdAndId(otherMember.getId(), invitation.getId());

    assertThat(unchangedInvitation)
        .as("초대가 존재해야 합니다")
        .isPresent();

    assertThat(unchangedInvitation.get().getStatus())
        .as("초대 상태가 변경되지 않아야 합니다")
        .isEqualTo(FriendInvitationStatus.PENDING);
  }

  @Test
  @DisplayName("이미 응답한 초대를 다시 응답하려 할 때 409 응답")
  void update_already_responded_invitation_then_return_409() throws Exception {
    // given - 이미 수락된 초대
    FriendInvitation invitation = FriendInvitation.builder()
        .sender(sender)
        .receiver(receiver)
        .status(FriendInvitationStatus.ACCEPTED)
        .build();
    friendInvitationRepository.save(invitation);

    UpdateReceivedInvitationRequest request = new UpdateReceivedInvitationRequest(FriendInvitationStatus.REJECTED);

    // when & then
    mockMvc.perform(patch(FRIEND_API_URL + "/requests/{invitationId}", invitation.getId())
            .header(HttpHeaders.AUTHORIZATION, receiverToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value(FriendExceptionSpec.INVITATION_ALREADY_RESPONDED.getCode()));

    // then - 초대 상태는 ACCEPTED 유지
    Optional<FriendInvitation> unchangedInvitation = friendInvitationRepository
        .findByReceiver_IdAndId(receiver.getId(), invitation.getId());

    assertThat(unchangedInvitation)
        .as("초대가 존재해야 합니다")
        .isPresent();

    assertThat(unchangedInvitation.get().getStatus())
        .as("초대 상태가 ACCEPTED로 유지되어야 합니다")
        .isEqualTo(FriendInvitationStatus.ACCEPTED);
  }

  @Test
  @DisplayName("양방향 초대가 있을 때 한 쪽 수락시 양쪽 모두 수락 처리")
  void accept_bidirectional_invitation_then_both_accepted() throws Exception {
    // given - 양방향 초대
    FriendInvitation invitation1 = FriendInvitation.create(sender, receiver);
    friendInvitationRepository.save(invitation1);

    FriendInvitation invitation2 = FriendInvitation.create(receiver, sender);
    friendInvitationRepository.save(invitation2);

    UpdateReceivedInvitationRequest request = new UpdateReceivedInvitationRequest(FriendInvitationStatus.ACCEPTED);

    // when - receiver가 invitation1 수락
    mockMvc.perform(patch(FRIEND_API_URL + "/requests/{invitationId}", invitation1.getId())
            .header(HttpHeaders.AUTHORIZATION, receiverToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // then - 양쪽 초대 모두 ACCEPTED
    Optional<FriendInvitation> updatedInvitation1 = friendInvitationRepository
        .findByReceiver_IdAndId(receiver.getId(), invitation1.getId());

    Optional<FriendInvitation> updatedInvitation2 = friendInvitationRepository
        .findByReceiver_IdAndId(sender.getId(), invitation2.getId());

    assertThat(updatedInvitation1)
        .as("첫 번째 초대가 존재해야 합니다")
        .isPresent();

    assertThat(updatedInvitation1.get().getStatus())
        .as("첫 번째 초대가 ACCEPTED여야 합니다")
        .isEqualTo(FriendInvitationStatus.ACCEPTED);

    assertThat(updatedInvitation2)
        .as("두 번째 초대가 존재해야 합니다")
        .isPresent();

    assertThat(updatedInvitation2.get().getStatus())
        .as("두 번째 초대도 ACCEPTED여야 합니다")
        .isEqualTo(FriendInvitationStatus.ACCEPTED);

    // then - 친구 관계 생성 검증
    boolean isFriend = friendshipRepository.isExist(sender.getId(), receiver.getId())
        || friendshipRepository.isExist(receiver.getId(), sender.getId());

    assertThat(isFriend)
        .as("친구 관계가 생성되어야 합니다")
        .isTrue();
  }
}