package site.praytogether.pray_together.exception;

import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // API BaseException
  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ExceptionResponse> handleBaseException(BaseException e) {
    log.error(e.getLogMessage());
    ExceptionSpec exceptionSpec = e.getExceptionSpec();
    return ResponseEntity.status(exceptionSpec.getStatus())
        .body(
            ExceptionResponse.of(
                exceptionSpec.getStatus().value(), exceptionSpec.getCode(), e.getClientMessage()));
  }

  // 유효성 검사 실패(@Valid)
  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest reques) {
    Map<String, String> fields = extractValidationFields(ex);
    logValidationFeilds(fields);
    return ResponseEntity.badRequest().body(ExceptionResponse.of(ex));
  }

  private void logValidationFeilds(Map<String, String> fields) {
    StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
    fields.forEach((key, value) -> joiner.add(key + "=" + value));
    String message = String.format("[ERROR] 유효성 검사 실패 : %s", joiner);
    logger.error(message);
  }

  private Map<String, String> extractValidationFields(BindException e) {
    return e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
  }
}
