package site.praytogether.pray_together.domain.auth.domain;

import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PasswordReissuer {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final int PASSWORD_LENGTH = 8;
  private final SecureRandom secureRandom;

  public String generatedByRandom() {
    StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
    for (int i = 0; i < PASSWORD_LENGTH; i++) {
      int randomIndex = secureRandom.nextInt(CHARACTERS.length());
      password.append(CHARACTERS.charAt(randomIndex));
    }
    return password.toString();
  }
}
