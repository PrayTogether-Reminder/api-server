package site.praytogether.pray_together.exception;

import static site.praytogether.pray_together.exception.spec.MethodArgumentExceptionSpec.METHOD_ARGUMENT_NOT_VALIDATION;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionResponse {
  private final int status;
  private final String code;
  private final String message;

  public static ExceptionResponse of(int status, String code, String message) {
    return new ExceptionResponse(status, code, message);
  }

  public static ExceptionResponse of(MethodArgumentNotValidException e) {
    return new ExceptionResponse(
        METHOD_ARGUMENT_NOT_VALIDATION.getStatus().value(),
        METHOD_ARGUMENT_NOT_VALIDATION.getCode(),
        e.getBindingResult().getFieldError().getDefaultMessage());
  }
}
