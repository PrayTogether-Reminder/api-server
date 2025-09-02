package site.praytogether.pray_together.domain.friend.domain.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

@Getter
@AllArgsConstructor
public enum FriendExceptionSpec implements ExceptionSpec {

  FRIENDSHIP_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "FRIIEND-001", "이미 친구 관계가 있습니다.");

  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
