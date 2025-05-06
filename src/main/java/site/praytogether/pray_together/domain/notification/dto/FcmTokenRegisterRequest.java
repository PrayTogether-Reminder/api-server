package site.praytogether.pray_together.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class FcmTokenRegisterRequest {
  private final String fcmToken;
}
