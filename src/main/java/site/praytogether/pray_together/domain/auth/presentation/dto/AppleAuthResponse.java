package site.praytogether.pray_together.domain.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AppleAuthResponse {
  private final String accessToken;
  private final String refreshToken;

  @JsonProperty("needsPhoneNumber")
  private final boolean needsPhoneNumber;

  public static AppleAuthResponse of(String accessToken, String refreshToken, boolean needsPhoneNumber) {
    return AppleAuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .needsPhoneNumber(needsPhoneNumber)
        .build();
  }
}
