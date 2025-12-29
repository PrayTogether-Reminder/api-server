package site.praytogether.pray_together.domain.auth.domain;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.auth.domain.exception.SocialLoginPasswordNotSupportedException;
import site.praytogether.pray_together.domain.member.model.Member;

@Component
@RequiredArgsConstructor
public class AuthService {

  public void validateLocalAuthentication(Member member) {
    if (!Objects.equals(member.getProvider(), OAuthProvider.LOCAL)) {
      throw new SocialLoginPasswordNotSupportedException(member.getId());
    }
  }
}
