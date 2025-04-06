package site.praytogether.pray_together.exception.spec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalExceptionSpec implements ExceptionSpec {
  METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "REQUEST_VALID-001", "요청 파라미터가 유효하지 않습니다."),
  CONSTRAINT_VIOLATE(HttpStatus.BAD_REQUEST, "REQUEST_VALID-002", "요청 파라미터가 제약 조건을 위반했습니다."),
  METHOD_ARGUMENT_TYPE_MISMATCH(
      HttpStatus.BAD_REQUEST, "REQUEST_VALID-003", "요청 파라미터를 올바르게 변환할 수 없습니다."),
  UNKNOWN_EXCEPTION(HttpStatus.BAD_REQUEST, "REQUEST_VALID-999", "공통 예외 처리기로 처리할 수 없는 예외가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
