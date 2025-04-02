package site.praytogether.pray_together.exception.spec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MethodArgumentExceptionSpec implements ExceptionSpec {
  METHOD_ARGUMENT_NOT_VALIDATION(HttpStatus.BAD_REQUEST, "METHOD-001", "올바른 값이 아닙니다."),
  ;
  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
