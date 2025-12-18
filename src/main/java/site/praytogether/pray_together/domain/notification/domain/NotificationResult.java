package site.praytogether.pray_together.domain.notification.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.praytogether.pray_together.domain.fcm_token.model.FcmToken;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResult {

  private final List<FcmToken> successTokens;
  private final List<FcmToken> failedTokens;

  public static NotificationResult of(List<FcmToken> successTokens, List<FcmToken> failedTokens) {
    return new NotificationResult(successTokens, failedTokens);
  }

  public boolean hasFailed() {
    return !failedTokens.isEmpty();
  }

  public int getSuccessCount() {
    return successTokens.size();
  }

  public int getFailedCount() {
    return failedTokens.size();
  }
}
