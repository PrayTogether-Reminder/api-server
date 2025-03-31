package site.praytogether.pray_together.exception.spec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberRoomExceptionSpec implements ExceptionSpec {
  MEMBER_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_ROOM-001", "회원이 속한 방을 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
