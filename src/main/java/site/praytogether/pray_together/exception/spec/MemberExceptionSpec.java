package site.praytogether.pray_together.exception.spec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionSpec implements ExceptionSpec {
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-001", "회원 정보를 찾을 수 없습니다."),
  MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "MEMBER-002", "이미 존재하는 회원 입니다."),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}
