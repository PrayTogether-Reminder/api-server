package site.praytogether.pray_together.test_config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.auth.domain.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.security.service.JwtService;

/**
 * 인증 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestAuthUtils {

  private final JwtService jwtService;

  public HttpHeaders create_Auth_HttpHeader_With_Member(Member member) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(issueAccessToken(member));
    return headers;
  }

  public String createBearerToken(Member member) {
    return "Bearer " + issueAccessToken(member);
  }

  private String issueAccessToken(Member member) {
    return jwtService.issueAccessToken(
        PrayTogetherPrincipal.builder()
            .id(member.getId())
            .email(member.getEmail())
            .build());
  }
}
