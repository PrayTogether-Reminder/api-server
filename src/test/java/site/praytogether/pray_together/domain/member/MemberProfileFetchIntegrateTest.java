package site.praytogether.pray_together.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import site.praytogether.pray_together.domain.member.dto.MemberProfileResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("회원 프로필 조회 테스트")
public class MemberProfileFetchIntegrateTest extends IntegrateTest {

  private HttpHeaders headers;
  private Member member;

  @BeforeEach
  void setup() {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    // 인증 헤더 생성
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @Test
  @DisplayName("회원 프로필 조회 API 요청 시 200 OK와 프로필 정보 응답")
  void fetch_member_profile_then_return_200_ok_with_profile() {
    // given
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // when
    ResponseEntity<MemberProfileResponse> responseEntity =
        restTemplate.exchange(
            MEMBERS_API_URL + "/me", HttpMethod.GET, requestEntity, MemberProfileResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("회원 프로필 조회 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    MemberProfileResponse response = responseEntity.getBody();
    assertThat(response).as("회원 프로필 조회 API 응답 결과가 NULL 입니다.").isNotNull();

    assertThat(response.getId()).as("응답된 회원 ID가 요청한 회원의 ID와 일치하지 않습니다.").isEqualTo(member.getId());

    assertThat(response.getName())
        .as("응답된 회원 이름이 요청한 회원의 이름과 일치하지 않습니다.")
        .isEqualTo(member.getName());

    assertThat(response.getEmail())
        .as("응답된 회원 이메일이 요청한 회원의 이메일과 일치하지 않습니다.")
        .isEqualTo(member.getEmail());
  }
}
