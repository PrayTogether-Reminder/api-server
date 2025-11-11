package site.praytogether.pray_together.domain.base;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageResponse {
  private final String message;

  public static MessageResponse of(String msg) {
    return new MessageResponse(msg);
  }
}
