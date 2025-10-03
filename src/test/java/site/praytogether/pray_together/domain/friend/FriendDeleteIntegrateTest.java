package site.praytogether.pray_together.domain.friend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("친구 삭제 통합 테스트")
public class FriendDeleteIntegrateTest extends IntegrateTest {
  private final String DELETE_FRIEND_URL = FRIEND_API_URL + "/{friendId}";

  private Member currentMember;
  private Member friend1;
  private Member friend2;
  private String currentMemberToken;

  @BeforeEach
  void setup() {
    currentMember = testUtils.createUniqueMember();
    memberRepository.save(currentMember);

    friend1 = testUtils.createUniqueMember();
    memberRepository.save(friend1);

    friend2 = testUtils.createUniqueMember();
    memberRepository.save(friend2);

    currentMemberToken = testUtils.createBearerToken(currentMember);
  }

  @Test
  @DisplayName("친구 관계 정상 삭제")
  void delete_friend_when_friendship_exists_then_success() throws Exception {
    // given
    Friendship friendship = Friendship.create(currentMember, friend1);
    friendshipRepository.save(friendship);

    // when & then
    mockMvc.perform(delete(DELETE_FRIEND_URL, friend1.getId())
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("친구 관계를 삭제했습니다."));

    // verify - 친구 관계가 삭제되었는지 확인
    Long smallerId = Math.min(currentMember.getId(), friend1.getId());
    Long biggerId = Math.max(currentMember.getId(), friend1.getId());
    boolean exists = friendshipRepository.isExist(smallerId, biggerId);
    assertThat(exists)
        .as("친구 관계가 삭제되어야 합니다")
        .isFalse();
  }

  @Test
  @DisplayName("존재하지 않는 친구 관계 삭제 시 예외")
  void delete_friend_when_friendship_not_exists_then_throw_exception() throws Exception {
    // given - 친구 관계 없이

    // when & then
    mockMvc.perform(delete(DELETE_FRIEND_URL, friend1.getId())
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("FRIEND-006"));
  }

  @Test
  @DisplayName("자기 자신 삭제 시도 시 예외")
  void delete_friend_when_self_deletion_then_throw_exception() throws Exception {
    // when & then
    mockMvc.perform(delete(DELETE_FRIEND_URL, currentMember.getId())
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("FRIEND-007"));
  }

  @Test
  @DisplayName("존재하지 않는 회원 ID로 삭제 시 예외")
  void delete_friend_when_member_not_exists_then_throw_exception() throws Exception {
    // given
    Long nonExistentMemberId = 99999L;

    // when & then
    mockMvc.perform(delete(DELETE_FRIEND_URL, nonExistentMemberId)
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MEMBER-001"));
  }

  @Test
  @DisplayName("member1/member2 위치와 무관하게 삭제 성공")
  void delete_friend_regardless_of_member_position() throws Exception {
    // given - friend1과 friend2를 각각 다른 방식으로 친구 관계 생성
    Friendship friendship1 = Friendship.create(currentMember, friend1);
    friendshipRepository.save(friendship1);

    Friendship friendship2 = Friendship.create(friend2, currentMember);
    friendshipRepository.save(friendship2);

    // when - friend1 삭제
    mockMvc.perform(delete(DELETE_FRIEND_URL, friend1.getId())
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isOk());

    // then - friend1과의 관계만 삭제되고 friend2는 유지
    Long smallerId1 = Math.min(currentMember.getId(), friend1.getId());
    Long biggerId1 = Math.max(currentMember.getId(), friend1.getId());
    boolean exists1 = friendshipRepository.isExist(smallerId1, biggerId1);
    assertThat(exists1)
        .as("friend1과의 관계는 삭제되어야 합니다")
        .isFalse();

    Long smallerId2 = Math.min(currentMember.getId(), friend2.getId());
    Long biggerId2 = Math.max(currentMember.getId(), friend2.getId());
    boolean exists2 = friendshipRepository.isExist(smallerId2, biggerId2);
    assertThat(exists2)
        .as("friend2와의 관계는 유지되어야 합니다")
        .isTrue();
  }

  @Test
  @DisplayName("이미 삭제된 친구 관계 재삭제 시 예외")
  void delete_friend_when_already_deleted_then_throw_exception() throws Exception {
    // given - 친구 관계 생성 후 삭제
    Friendship friendship = Friendship.create(currentMember, friend1);
    friendshipRepository.save(friendship);

    mockMvc.perform(delete(DELETE_FRIEND_URL, friend1.getId())
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isOk());

    // when & then - 재삭제 시도
    mockMvc.perform(delete(DELETE_FRIEND_URL, friend1.getId())
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("FRIEND-006"));
  }
}