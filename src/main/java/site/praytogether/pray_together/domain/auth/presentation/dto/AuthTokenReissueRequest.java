package site.praytogether.pray_together.domain.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
public class AuthTokenReissueRequest {
  @NotBlank(message = "다시 로그인해 주세요.")
  String refreshToken;
}
