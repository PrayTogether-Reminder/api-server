package site.praytogether.pray_together.exception.spec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberRoomExceptionSpec implements ExceptionSpec {
  MEMBER_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_ROOM-001", "회원이 속한 방을 찾을 수 없습니다."),
  MEMBER_ROOM_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER_ROOM-002", "이미 방에 존재하는 회원입니다."),
  SOMONE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER_ROOM-003", "이미 방에 존재하는 회원이 있습니다.");

  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
