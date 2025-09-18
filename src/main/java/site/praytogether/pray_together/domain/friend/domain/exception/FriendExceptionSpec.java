package site.praytogether.pray_together.domain.friend.domain.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

@Getter
@AllArgsConstructor
public enum FriendExceptionSpec implements ExceptionSpec {

  FRIENDSHIP_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "FRIEND-001", "이미 친구 관계가 있습니다."),
  FRIEND_INVITATION_NOF_FOUND(HttpStatus.NOT_FOUND, "FRIEND-002", "친구 초대장을 찾을 수 없습니다."),
  INVITATION_ALREADY_RESPONDED(HttpStatus.CONFLICT, "FRIEND-003", "이미 응답된 친구 초대입니다."),
  SELF_INVITATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FRIEND-004", "자기 자신에게 친구 요청을 보낼 수 없습니다."),
  DUPLICATE_INVITATION(HttpStatus.CONFLICT, "FRIEND-005", "이미 친구 요청을 보낸 상태입니다.");

  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
