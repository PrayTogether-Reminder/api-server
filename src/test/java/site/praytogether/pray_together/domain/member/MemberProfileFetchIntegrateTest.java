package site.praytogether.pray_together.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.member.dto.MemberProfileResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("회원 프로필 조회 테스트")
public class MemberProfileFetchIntegrateTest extends IntegrateTest {

  private String token;
  private Member member;

  @BeforeEach
  void setup() throws Exception {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    // 인증 토큰 생성
    token = testUtils.createBearerToken(member);
  }

  @Test
  @DisplayName("회원 프로필 조회 API 요청 시 200 OK와 프로필 정보 응답")
  void fetch_member_profile_then_return_200_ok_with_profile() throws Exception {
    // when
    MvcResult result = mockMvc.perform(get(MEMBERS_API_URL + "/me")
            .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    MemberProfileResponse response = objectMapper.readValue(responseBody, MemberProfileResponse.class);
    assertThat(response).as("회원 프로필 조회 API 응답 결과가 NULL 입니다.").isNotNull();

    assertThat(response.getId()).as("응답된 회원 ID가 요청한 회원의 ID와 일치하지 않습니다.").isEqualTo(member.getId());

    assertThat(response.getName())
        .as("응답된 회원 이름이 요청한 회원의 이름과 일치하지 않습니다.")
        .isEqualTo(member.getName());

    assertThat(response.getEmail())
        .as("응답된 회원 이메일이 요청한 회원의 이메일과 일치하지 않습니다.")
        .isEqualTo(member.getEmail());

    assertThat(response.getPhoneNumber())
        .as("응답된 회원 전화번호가 요청한 회원의 전화번호와 일치하지 않습니다.")
        .isEqualTo(member.getPhoneNumber().getValue());
  }

  @Test
  @DisplayName("전화번호가 null인 회원 프로필 조회 시 200 OK와 전화번호 null 응답")
  void fetch_member_profile_with_null_phone_number_then_return_200_ok() throws Exception {
    // given - 전화번호가 없는 회원 생성
    Member memberWithoutPhone = Member.builder()
        .name("전화번호없는회원")
        .email("nophone@test.com")
        .password("test-password")
        .phoneNumber(null)
        .build();
    memberRepository.save(memberWithoutPhone);

    String tokenWithoutPhone = testUtils.createBearerToken(memberWithoutPhone);

    // when
    MvcResult result = mockMvc.perform(get(MEMBERS_API_URL + "/me")
            .header(HttpHeaders.AUTHORIZATION, tokenWithoutPhone))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    MemberProfileResponse response = objectMapper.readValue(responseBody, MemberProfileResponse.class);

    assertThat(response).as("회원 프로필 조회 API 응답 결과가 NULL 입니다.").isNotNull();
    assertThat(response.getId()).as("응답된 회원 ID가 요청한 회원의 ID와 일치하지 않습니다.").isEqualTo(memberWithoutPhone.getId());
    assertThat(response.getName()).as("응답된 회원 이름이 요청한 회원의 이름과 일치하지 않습니다.").isEqualTo(memberWithoutPhone.getName());
    assertThat(response.getEmail()).as("응답된 회원 이메일이 요청한 회원의 이메일과 일치하지 않습니다.").isEqualTo(memberWithoutPhone.getEmail());
    assertThat(response.getPhoneNumber()).as("전화번호가 없는 회원의 경우 응답의 전화번호도 NULL이어야 합니다.").isNull();
  }
}