package site.praytogether.pray_together.security.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {
  private final JwtException jwtException;

  public JwtAuthenticationException(JwtException exception) {
    super(exception.getMessage(), exception);
    this.jwtException = exception;
  }
}
