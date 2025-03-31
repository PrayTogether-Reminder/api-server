package site.praytogether.pray_together.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
}
