package site.praytogether.pray_together.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.praytogether.pray_together.domain.auth.presentation.dto.ChangePasswordRequest;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.PhoneNumber;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("비밀번호 변경 통합 테스트")
public class ChangePasswordIntegrateTest extends IntegrateTest {

  private Member member;
  private String token;
  private final String CHANGE_PASSWORD_URL = AUTH_API_URL + "/password";

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    member = testMemberUtils.createSave();
    token = testAuthUtils.createBearerToken(member);
  }

  @Test
  @DisplayName("정상적인 요청으로 비밀번호 변경 시 200 OK 및 비밀번호 업데이트")
  void change_password_with_valid_input_then_return_200_ok() throws Exception {
    String newPassword = "newPassword123!";
    ChangePasswordRequest request = new ChangePasswordRequest(newPassword);

    mockMvc.perform(patch(CHANGE_PASSWORD_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("비밀번호를 변경했습니다."));

    Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
    assertThat(passwordEncoder.matches(newPassword, updatedMember.getPassword())).isTrue();
  }

  @Test
  @DisplayName("새 비밀번호가 비어 있으면 400 Bad Request")
  void change_password_with_blank_new_password_then_return_400_bad_request() throws Exception {
    String originalPassword = memberRepository.findById(member.getId()).orElseThrow().getPassword();
    ChangePasswordRequest request = new ChangePasswordRequest("");

    mockMvc.perform(patch(CHANGE_PASSWORD_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
    assertThat(reloadedMember.getPassword()).isEqualTo(originalPassword);
  }

  @Test
  @DisplayName("Google OAuth 회원이 비밀번호 변경 시도 시 400 Bad Request")
  void change_password_with_google_member_then_return_400_bad_request() throws Exception {
    // given - Google OAuth 회원 생성
    Member googleMember = Member.createGoogleMember(
        "구글회원",
        "google@gmail.com",
        PhoneNumber.of("010-9999-8888"));
    memberRepository.save(googleMember);
    String googleToken = testAuthUtils.createBearerToken(googleMember);

    ChangePasswordRequest request = new ChangePasswordRequest("newPassword123!");

    // when & then
    mockMvc.perform(patch(CHANGE_PASSWORD_URL)
            .header(HttpHeaders.AUTHORIZATION, googleToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // 비밀번호가 여전히 null인지 확인
    Member reloadedMember = memberRepository.findById(googleMember.getId()).orElseThrow();
    assertThat(reloadedMember.getPassword()).isNull();
  }
}
