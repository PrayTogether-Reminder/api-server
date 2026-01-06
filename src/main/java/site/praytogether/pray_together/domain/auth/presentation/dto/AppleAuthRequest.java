package site.praytogether.pray_together.domain.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppleAuthRequest {

  @NotBlank(message = "Identity Token을 입력해 주세요.")
  private final String identityToken;

  private final String authorizationCode;

  private final String name;
}
