package site.praytogether.pray_together.exception;

import static site.praytogether.pray_together.exception.spec.GlobalExceptionSpec.CONSTRAINT_VIOLATE;
import static site.praytogether.pray_together.exception.spec.GlobalExceptionSpec.METHOD_ARGUMENT_NOT_VALID;
import static site.praytogether.pray_together.exception.spec.GlobalExceptionSpec.METHOD_ARGUMENT_TYPE_MISMATCH;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        METHOD_ARGUMENT_NOT_VALID.getStatus().value(),
        METHOD_ARGUMENT_NOT_VALID.getCode(),
        e.getBindingResult().getFieldError().getDefaultMessage());
  }

  public static ExceptionResponse of(ConstraintViolationException ex) {
    ConstraintViolation<?> violation = ex.getConstraintViolations().stream().findFirst().get();
    return new ExceptionResponse(
        CONSTRAINT_VIOLATE.getStatus().value(),
        CONSTRAINT_VIOLATE.getCode(),
        violation.getMessage());
  }

  public static ExceptionResponse of(MethodArgumentTypeMismatchException ex) {
    return new ExceptionResponse(
        METHOD_ARGUMENT_TYPE_MISMATCH.getStatus().value(),
        METHOD_ARGUMENT_TYPE_MISMATCH.getCode(),
        "올바른 요청이 아닙니다.");
  }

  public static ExceptionResponse of(Exception ex) {
    return new ExceptionResponse(
        METHOD_ARGUMENT_TYPE_MISMATCH.getStatus().value(),
        METHOD_ARGUMENT_TYPE_MISMATCH.getCode(),
        "알 수 없는 오류가 발생했습니다.");
  }
}
