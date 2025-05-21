package site.praytogether.pray_together.domain.fcm_token.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class FcmTokenRegisterRequest {

  @NotBlank(message = "토큰 값이 비어 있습니다.")
  private final String fcmToken;
}
