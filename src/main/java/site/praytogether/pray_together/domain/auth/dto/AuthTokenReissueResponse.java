package site.praytogether.pray_together.domain.auth.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthTokenReissueResponse {
  private final String accessToken;
  private final String refreshToken;

  public static AuthTokenReissueResponse of(String accessToken, String refreshToken) {
    return new AuthTokenReissueResponse(accessToken, refreshToken);
  }
}
