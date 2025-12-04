package site.praytogether.pray_together.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import site.praytogether.pray_together.domain.auth.domain.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.auth.domain.RefreshToken;
import site.praytogether.pray_together.domain.auth.presentation.dto.AuthTokenReissueRequest;
import site.praytogether.pray_together.domain.auth.presentation.dto.AuthTokenReissueResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.security.service.JwtService;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("토큰 재발급 통합 테스트")
public class AuthTokenReissueIntegrateTest extends IntegrateTest {

  @Autowired
  private JwtService jwtService;

  private Member member;
  private String refreshToken;
  private final String REISSUE_TOKEN_URL = AUTH_API_URL + "/reissue-token";

  @BeforeEach
  void setUp() {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    // Refresh Token 생성 및 저장
    PrayTogetherPrincipal principal = PrayTogetherPrincipal.builder()
        .id(member.getId())
        .email(member.getEmail())
        .build();
    refreshToken = jwtService.issueRefreshToken(principal);
    Instant expiration = jwtService.extractExpiration(refreshToken);

    RefreshToken refreshTokenEntity = RefreshToken.create(member, refreshToken, expiration);
    refreshTokenRepository.save(refreshTokenEntity);
  }

  @Test
  @DisplayName("정상적인 Refresh Token으로 재발급 시 200 OK 및 새로운 토큰 발급")
  void reissue_token_with_valid_refresh_token_then_return_200_ok() throws Exception {
    // given
    AuthTokenReissueRequest request = new AuthTokenReissueRequest(refreshToken);

    // when & then
    MvcResult result = mockMvc.perform(post(REISSUE_TOKEN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists())
        .andReturn();

    // 응답 결과 파싱
    String responseBody = result.getResponse().getContentAsString();
    AuthTokenReissueResponse response = objectMapper.readValue(responseBody, AuthTokenReissueResponse.class);

    // 새로운 토큰 검증
    assertThat(response.getAccessToken()).isNotNull();
    assertThat(response.getRefreshToken()).isNotNull();
    assertThat(response.getRefreshToken()).isNotEqualTo(refreshToken);
    assertThatCode(() -> jwtService.isValid(response.getRefreshToken()))
        .as("재발급된 Refresh Token 검증 시 예외가 발생하면 안 됩니다.")
        .doesNotThrowAnyException();
    assertThatCode(() -> jwtService.isValid(response.getAccessToken()))
        .as("재발급된 Refresh Token 검증 시 예외가 발생하면 안 됩니다.")
        .doesNotThrowAnyException();

    // DB 검증 - 기존 Refresh Token 삭제 후 새로운 토큰 저장 확인
    RefreshToken savedToken = refreshTokenRepository.findByMemberId(member.getId()).orElse(null);
    assertThat(savedToken).isNotNull();
    assertThat(savedToken.getToken()).isEqualTo(response.getRefreshToken());
  }

  @Test
  @DisplayName("유효하지 않은 Refresh Token으로 재발급 시 400 Bad Request")
  void reissue_token_with_invalid_refresh_token_then_return_400_bad_request() throws Exception {
    // given
    String invalidToken = "invalid.refresh.token";
    AuthTokenReissueRequest request = new AuthTokenReissueRequest(invalidToken);

    // when & then
    mockMvc.perform(post(REISSUE_TOKEN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("DB에 존재하지 않는 Refresh Token으로 재발급 시 400 Bad Request")
  void reissue_token_with_non_existent_token_then_return_400_bad_request() throws Exception {
    // given - 다른 회원의 Refresh Token 생성 (DB에 저장하지 않음)
    Member otherMember = testUtils.createUniqueMember();
    memberRepository.save(otherMember);

    PrayTogetherPrincipal principal = PrayTogetherPrincipal.builder()
        .id(otherMember.getId())
        .email(otherMember.getEmail())
        .build();
    String nonExistentToken = jwtService.issueRefreshToken(principal);
    AuthTokenReissueRequest request = new AuthTokenReissueRequest(nonExistentToken);

    // when & then
    mockMvc.perform(post(REISSUE_TOKEN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Access Token으로 재발급 시도 시 400 Bad Request")
  void reissue_token_with_access_token_then_return_400_bad_request() throws Exception {
    // given - Access Token 생성
    PrayTogetherPrincipal principal = PrayTogetherPrincipal.builder()
        .id(member.getId())
        .email(member.getEmail())
        .build();
    String accessToken = jwtService.issueAccessToken(principal);
    AuthTokenReissueRequest request = new AuthTokenReissueRequest(accessToken);

    // when & then
    mockMvc.perform(post(REISSUE_TOKEN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
