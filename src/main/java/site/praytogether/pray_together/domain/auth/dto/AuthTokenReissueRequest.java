package site.praytogether.pray_together.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthTokenReissueRequest {
  @NotBlank(message = "다시 로그인해 주세요.")
  private final String refreshToken;
}
