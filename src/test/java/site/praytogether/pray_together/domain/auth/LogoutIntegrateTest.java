package site.praytogether.pray_together.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import site.praytogether.pray_together.domain.auth.model.RefreshToken;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("로그아웃 통합 테스트")
public class LogoutIntegrateTest extends IntegrateTest {

  private Member member;
  private String token;
  private final String LOGOUT_URL = AUTH_API_URL + "/logout";

  @BeforeEach
  void setUp() {
    member = testUtils.createUniqueMember();
    memberRepository.save(member);
    token = testUtils.createBearerToken(member);

    RefreshToken refreshToken =
        RefreshToken.create(member, "test-refresh-token", Instant.now().plusSeconds(3600));
    refreshTokenRepository.save(refreshToken);
  }

  @Test
  @DisplayName("로그아웃 시 Refresh Token 삭제 및 204 No Content 응답")
  void logout_then_delete_refresh_token_and_return_204() throws Exception {
    assertThat(refreshTokenRepository.existsByMemberId(member.getId())).isTrue();

    mockMvc
        .perform(
            post(LOGOUT_URL)
                .servletPath(LOGOUT_URL)
                .header(HttpHeaders.AUTHORIZATION, token))
        .andExpect(status().isNoContent());

    assertThat(refreshTokenRepository.existsByMemberId(member.getId())).isFalse();
  }
}
