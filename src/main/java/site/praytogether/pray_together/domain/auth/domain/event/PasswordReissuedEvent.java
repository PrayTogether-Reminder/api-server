package site.praytogether.pray_together.domain.auth.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PasswordReissuedEvent {
  private final String email;
  private final String newPassword;

  public static PasswordReissuedEvent of(String email, String newPassword) {
    return new PasswordReissuedEvent(email, newPassword);
  }
}
