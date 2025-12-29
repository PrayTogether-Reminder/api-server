package site.praytogether.pray_together.domain.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleAuthResponse {
  @JsonProperty("isNewMember")
  private final boolean isNewMember;
  private final String accessToken;
  private final String refreshToken;

  public static GoogleAuthResponse newMember() {
    return GoogleAuthResponse.builder()
        .isNewMember(true)
        .build();
  }

  public static GoogleAuthResponse existingMember(String accessToken, String refreshToken) {
    return GoogleAuthResponse.builder()
        .isNewMember(false)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }
}
