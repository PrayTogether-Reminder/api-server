package site.praytogether.pray_together.test_config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.security.service.JwtService;

@Component
@RequiredArgsConstructor
public class TestUtils {

  private static int emailUnique = 0;
  private final JwtService jwtService;

  public Member createMember() {
    return Member.create("test", "test@test.com" + (emailUnique++), "test");
  }

  public HttpHeaders create_Auth_HttpHeader_With_Member(Member member) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(
        jwtService.issueAccessToken(
            PrayTogetherPrincipal.builder().id(member.getId()).email(member.getEmail()).build()));
    return headers;
  }
}
