package site.praytogether.pray_together.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import site.praytogether.pray_together.domain.member.dto.UpdateProfileRequest;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("회원 프로필 업데이트 테스트")
public class MemberProfileUpdateIntegrateTest extends IntegrateTest {

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
  @DisplayName("이름과 전화번호 모두 업데이트 시 200 OK 응답 및 정상 업데이트")
  void update_name_and_phone_then_return_200_ok() throws Exception {
    // given
    UpdateProfileRequest request = new UpdateProfileRequest("새이름", "010-9999-8888");
    String requestBody = objectMapper.writeValueAsString(request);

    // when
    mockMvc.perform(patch(MEMBERS_API_URL + "/me")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("프로필을 변경했습니다."));

    // then
    Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
    assertThat(updatedMember.getName()).isEqualTo("새이름");
    assertThat(updatedMember.getPhoneNumber().getValue()).isEqualTo("010-9999-8888");
  }

  @Test
  @DisplayName("이름만 업데이트 시 200 OK 응답 및 이름만 변경")
  void update_only_name_then_return_200_ok() throws Exception {
    // given
    String originalPhone = member.getPhoneNumber().getValue();
    UpdateProfileRequest request = new UpdateProfileRequest("변경된이름", null);
    String requestBody = objectMapper.writeValueAsString(request);

    // when
    mockMvc.perform(patch(MEMBERS_API_URL + "/me")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    // then
    Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
    assertThat(updatedMember.getName()).isEqualTo("변경된이름");
    assertThat(updatedMember.getPhoneNumber().getValue()).isEqualTo(originalPhone);
  }

  @Test
  @DisplayName("전화번호만 업데이트 시 200 OK 응답 및 전화번호만 변경")
  void update_only_phone_then_return_200_ok() throws Exception {
    // given
    String originalName = member.getName();
    UpdateProfileRequest request = new UpdateProfileRequest(null, "01087654321");
    String requestBody = objectMapper.writeValueAsString(request);

    // when
    mockMvc.perform(patch(MEMBERS_API_URL + "/me")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk());

    // then
    Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();
    assertThat(updatedMember.getName()).isEqualTo(originalName);
    assertThat(updatedMember.getPhoneNumber().getValue()).isEqualTo("010-8765-4321");
  }

  @Test
  @DisplayName("잘못된 전화번호 형식으로 업데이트 시 400 Bad Request 응답")
  void update_with_invalid_phone_format_then_return_400() throws Exception {
    // given
    UpdateProfileRequest request = new UpdateProfileRequest("이름", "010-123-4567");
    String requestBody = objectMapper.writeValueAsString(request);

    // when & then
    mockMvc.perform(patch(MEMBERS_API_URL + "/me")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("올바른 휴대폰 번호 형식이 아닙니다."));
  }

  @Test
  @DisplayName("이름이 10자 초과 시 400 Bad Request 응답")
  void update_with_name_too_long_then_return_400() throws Exception {
    // given
    UpdateProfileRequest request = new UpdateProfileRequest("열한글자이상의이름입니다", "010-1234-5678");
    String requestBody = objectMapper.writeValueAsString(request);

    // when & then
    mockMvc.perform(patch(MEMBERS_API_URL + "/me")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("이름은 1자 이상 10자 이하로 입력해 주세요."));
  }

  @Test
  @DisplayName("허용되지 않은 전화번호 prefix로 업데이트 시 400 Bad Request 응답")
  void update_with_invalid_phone_prefix_then_return_400() throws Exception {
    // given
    UpdateProfileRequest request = new UpdateProfileRequest("이름", "015-1234-5678");
    String requestBody = objectMapper.writeValueAsString(request);

    // when & then
    mockMvc.perform(patch(MEMBERS_API_URL + "/me")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("올바른 휴대폰 번호 형식이 아닙니다."));
  }
}
