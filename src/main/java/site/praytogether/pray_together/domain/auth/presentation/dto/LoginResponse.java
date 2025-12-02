package site.praytogether.pray_together.domain.auth.presentation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LoginResponse {
  private final String accessToken;
  private final String refreshToken;
}
