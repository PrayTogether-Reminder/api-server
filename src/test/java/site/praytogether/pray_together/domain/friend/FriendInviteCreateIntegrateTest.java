package site.praytogether.pray_together.domain.friend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static site.praytogether.pray_together.domain.friend.domain.exception.FriendExceptionSpec.*;
import static site.praytogether.pray_together.exception.spec.MemberExceptionSpec.MEMBER_NOT_FOUND;

import org.springframework.http.HttpHeaders;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitation;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationStatus;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("친구 초대 통합 테스트")
public class FriendInviteCreateIntegrateTest extends IntegrateTest {
  private Member sender;
  private Member receiver;
  private String senderToken;

  @BeforeEach
  void setup() throws Exception {
    sender = testUtils.createUniqueMember();
    memberRepository.save(sender);

    receiver = testUtils.createUniqueMember();
    memberRepository.save(receiver);

    senderToken = testUtils.createBearerToken(sender);
  }

  @Test
  @DisplayName("친구 초대 성공시 200 OK 응답")
  void invite_friend_then_return_200_ok() throws Exception {
    // when
    mockMvc.perform(post(FRIEND_API_URL + "/{inviteeId}/requests", receiver.getId())
            .header(HttpHeaders.AUTHORIZATION, senderToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").exists());

    // then
    Optional<FriendInvitation> invitation = friendInvitationRepository
        .findBySender_IdAndReceiver_IdAndStatus(sender.getId(), receiver.getId(), FriendInvitationStatus.PENDING);

    assertThat(invitation)
        .as("저장된 친구 초대가 존재해야 합니다.")
        .isPresent();

    assertThat(invitation.get().getSender().getId())
        .as("발신자 ID가 예상과 다릅니다.")
        .isEqualTo(sender.getId());

    assertThat(invitation.get().getReceiver().getId())
        .as("수신자 ID가 예상과 다릅니다.")
        .isEqualTo(receiver.getId());

    assertThat(invitation.get().getStatus())
        .as("초대 상태가 PENDING이 아닙니다.")
        .isEqualTo(FriendInvitationStatus.PENDING);
  }

  @Test
  @DisplayName("존재하지 않는 사용자를 초대시 404 응답")
  void invite_non_existent_user_then_return_404() throws Exception {
    // given
    Long nonExistentMemerId = 99999L;

    // when & then
    mockMvc.perform(post(FRIEND_API_URL + "/{inviteeId}/requests", nonExistentMemerId)
            .header(HttpHeaders.AUTHORIZATION, senderToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()));

    // then
    Optional<FriendInvitation> invitation = friendInvitationRepository
        .findBySender_IdAndReceiver_IdAndStatus(sender.getId(), nonExistentMemerId, FriendInvitationStatus.PENDING);

    assertThat(invitation)
        .as("친구 초대가 생성되지 않아야 합니다.")
        .isEmpty();
  }

  @Test
  @DisplayName("이미 친구인 사용자를 초대시 400 응답")
  void invite_existing_friend_then_return_400() throws Exception {
    // given
    Friendship friendship = Friendship.builder()
        .member1(sender)
        .member2(receiver)
        .build();
    friendshipRepository.save(friendship);

    // when & then
    mockMvc.perform(post(FRIEND_API_URL + "/{inviteeId}/requests", receiver.getId())
            .header(HttpHeaders.AUTHORIZATION, senderToken))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(FRIENDSHIP_ALREADY_EXIST.getCode()));

    // then
    Optional<FriendInvitation> invitation = friendInvitationRepository
        .findBySender_IdAndReceiver_IdAndStatus(sender.getId(), receiver.getId(), FriendInvitationStatus.PENDING);

    assertThat(invitation)
        .as("친구 초대가 생성되지 않아야 합니다.")
        .isEmpty();
  }

  @Test
  @DisplayName("이미 보낸 초대가 있을 때 재초대시 409 응답")
  void invite_with_pending_invitation_then_return_409() throws Exception {
    // given
    FriendInvitation existingInvitation = FriendInvitation.create(sender, receiver);
    friendInvitationRepository.save(existingInvitation);

    // when & then
    mockMvc.perform(post(FRIEND_API_URL + "/{inviteeId}/requests", receiver.getId())
            .header(HttpHeaders.AUTHORIZATION, senderToken))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value(DUPLICATE_INVITATION.getCode()));

    // then - 여전히 하나의 초대만 존재해야 함
    Optional<FriendInvitation> invitation = friendInvitationRepository
        .findBySender_IdAndReceiver_IdAndStatus(sender.getId(), receiver.getId(), FriendInvitationStatus.PENDING);

    assertThat(invitation)
        .as("기존 초대만 존재해야 합니다.")
        .isPresent();

    assertThat(invitation.get().getId())
        .as("기존 초대와 동일한 ID여야 합니다.")
        .isEqualTo(existingInvitation.getId());
  }

  @Test
  @DisplayName("자기 자신을 초대시 400 응답")
  void invite_self_then_return_400() throws Exception {
    // when & then
    mockMvc.perform(post(FRIEND_API_URL + "/{inviteeId}/requests", sender.getId())
            .header(HttpHeaders.AUTHORIZATION, senderToken))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(SELF_INVITATION_NOT_ALLOWED.getCode()));

    // then
    Optional<FriendInvitation> invitation = friendInvitationRepository
        .findBySender_IdAndReceiver_IdAndStatus(sender.getId(), sender.getId(), FriendInvitationStatus.PENDING);

    assertThat(invitation)
        .as("친구 초대가 생성되지 않아야 합니다.")
        .isEmpty();
  }

  @Test
  @DisplayName("상대방이 나를 이미 초대한 경우에도 내가 초대 가능")
  void invite_when_invitee_already_invited_me_then_return_200() throws Exception {
    // given
    FriendInvitation reverseInvitation = FriendInvitation.create(receiver, sender);
    friendInvitationRepository.save(reverseInvitation);

    // when & then
    mockMvc.perform(post(FRIEND_API_URL + "/{inviteeId}/requests", receiver.getId())
            .header(HttpHeaders.AUTHORIZATION, senderToken))
        .andExpect(status().isOk());

    // then - 양방향 초대 확인
    Optional<FriendInvitation> myInvitation = friendInvitationRepository
        .findBySender_IdAndReceiver_IdAndStatus(sender.getId(), receiver.getId(), FriendInvitationStatus.PENDING);
    Optional<FriendInvitation> theirInvitation = friendInvitationRepository
        .findBySender_IdAndReceiver_IdAndStatus(receiver.getId(), sender.getId(), FriendInvitationStatus.PENDING);

    assertThat(myInvitation)
        .as("내가 보낸 초대가 존재해야 합니다.")
        .isPresent();

    assertThat(theirInvitation)
        .as("상대방이 보낸 초대도 여전히 존재해야 합니다.")
        .isPresent();
  }
}