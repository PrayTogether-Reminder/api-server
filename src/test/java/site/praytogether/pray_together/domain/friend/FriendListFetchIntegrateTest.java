package site.praytogether.pray_together.domain.friend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.friend.domain.friendship.Friendship;
import site.praytogether.pray_together.domain.friend.presentation.dto.FetchFriendListResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("친구 목록 조회 통합 테스트")
public class FriendListFetchIntegrateTest extends IntegrateTest {
  private Member currentMember;
  private Member friend1;
  private Member friend2;
  private Member friend3;
  private Member notFriend;
  private String currentMemberToken;

  @BeforeEach
  void setup() throws Exception {
    currentMember = testUtils.createUniqueMember();
    memberRepository.save(currentMember);

    friend1 = testUtils.createUniqueMember();
    memberRepository.save(friend1);

    friend2 = testUtils.createUniqueMember();
    memberRepository.save(friend2);

    friend3 = testUtils.createUniqueMember();
    memberRepository.save(friend3);

    notFriend = testUtils.createUniqueMember();
    memberRepository.save(notFriend);

    currentMemberToken = testUtils.createBearerToken(currentMember);
  }

  @Test
  @DisplayName("친구가 없을 때 빈 목록 반환")
  void fetch_friend_list_when_no_friends_then_return_empty_list() throws Exception {
    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL)
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchFriendListResponse response = objectMapper.readValue(content, FetchFriendListResponse.class);

    assertThat(response.getFriends())
        .as("친구가 없어야 합니다")
        .isEmpty();
  }

  @Test
  @DisplayName("친구가 1명일 때 정상 조회")
  void fetch_friend_list_when_one_friend_then_return_list() throws Exception {
    // given
    Friendship friendship = Friendship.create(currentMember, friend1);
    friendshipRepository.save(friendship);

    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL)
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchFriendListResponse response = objectMapper.readValue(content, FetchFriendListResponse.class);

    assertThat(response.getFriends())
        .as("친구가 1명 조회되어야 합니다")
        .hasSize(1);

    assertThat(response.getFriends().get(0).getFriendId())
        .as("친구 ID가 올바르게 조회되어야 합니다")
        .isEqualTo(friend1.getId());

    assertThat(response.getFriends().get(0).getFriendName())
        .as("친구 이름이 올바르게 조회되어야 합니다")
        .isEqualTo(friend1.getName());
  }

  @Test
  @DisplayName("친구가 여러 명일 때 모두 조회")
  void fetch_friend_list_when_multiple_friends_then_return_all() throws Exception {
    // given
    Friendship friendship1 = Friendship.create(currentMember, friend1);
    friendshipRepository.save(friendship1);

    Friendship friendship2 = Friendship.create(currentMember, friend2);
    friendshipRepository.save(friendship2);

    Friendship friendship3 = Friendship.create(currentMember, friend3);
    friendshipRepository.save(friendship3);

    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL)
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchFriendListResponse response = objectMapper.readValue(content, FetchFriendListResponse.class);

    assertThat(response.getFriends())
        .as("3명의 친구가 모두 조회되어야 합니다")
        .hasSize(3);

    assertThat(response.getFriends())
        .extracting("friendId")
        .as("모든 친구 ID가 포함되어야 합니다")
        .containsExactlyInAnyOrder(friend1.getId(), friend2.getId(), friend3.getId());

    assertThat(response.getFriends())
        .extracting("friendName")
        .as("모든 친구 이름이 포함되어야 합니다")
        .containsExactlyInAnyOrder(friend1.getName(), friend2.getName(), friend3.getName());
  }

  @Test
  @DisplayName("member1/member2 위치와 무관하게 조회")
  void fetch_friend_list_regardless_of_member_position() throws Exception {
    // given - 두 케이스를 모두 생성
    // Friendship.create는 내부적으로 ID 크기 비교해서 member1/member2 결정
    Friendship friendship1 = Friendship.create(currentMember, friend1);
    friendshipRepository.save(friendship1);

    Friendship friendship2 = Friendship.create(friend2, currentMember);
    friendshipRepository.save(friendship2);

    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL)
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchFriendListResponse response = objectMapper.readValue(content, FetchFriendListResponse.class);

    assertThat(response.getFriends())
        .as("두 친구 모두 조회되어야 합니다")
        .hasSize(2);

    assertThat(response.getFriends())
        .extracting("friendId")
        .as("member1/member2 위치와 무관하게 친구가 조회되어야 합니다")
        .containsExactlyInAnyOrder(friend1.getId(), friend2.getId());
  }

  @Test
  @DisplayName("친구가 아닌 멤버는 조회되지 않음")
  void fetch_friend_list_excludes_non_friends() throws Exception {
    // given - friend1만 친구로 등록
    Friendship friendship = Friendship.create(currentMember, friend1);
    friendshipRepository.save(friendship);

    // notFriend는 Friendship에 등록하지 않음

    // when
    MvcResult result = mockMvc.perform(get(FRIEND_API_URL)
            .header(HttpHeaders.AUTHORIZATION, currentMemberToken))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String content = result.getResponse().getContentAsString();
    FetchFriendListResponse response = objectMapper.readValue(content, FetchFriendListResponse.class);

    assertThat(response.getFriends())
        .as("친구인 멤버만 조회되어야 합니다")
        .hasSize(1);

    assertThat(response.getFriends().get(0).getFriendId())
        .as("친구로 등록된 friend1만 조회되어야 합니다")
        .isEqualTo(friend1.getId());

    assertThat(response.getFriends())
        .extracting("friendId")
        .as("친구가 아닌 notFriend는 조회되지 않아야 합니다")
        .doesNotContain(notFriend.getId());
  }
}