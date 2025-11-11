package site.praytogether.pray_together.exception;

import static site.praytogether.pray_together.exception.spec.GlobalExceptionSpec.METHOD_ARGUMENT_NOT_VALID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

  // 요청 데이터 유효성 검사 실패 (@Valid)
  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest reques) {
    logMethodArgumentNotValid(ex);
    return ResponseEntity.badRequest().body(ExceptionResponse.of(ex));
  }

  private void logMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    Map<String, String> fields =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

    StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
    fields.forEach((key, value) -> joiner.add(key + "=" + value));
    String message = String.format("[ERROR] 유효성 검사 실패 : %s", joiner);
    logger.error(message);
  }

  // 요청 데이터 제약 조건 위반 (@Valid)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ExceptionResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    logConstraintViolationException(e);
    return ResponseEntity.badRequest().body(ExceptionResponse.of(e));
  }

  private void logConstraintViolationException(ConstraintViolationException e) {
    StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");

    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      String propertyPath = violation.getPropertyPath().toString();
      String message = violation.getMessage();
      Object invalidValue = violation.getInvalidValue();
      String detailMessage = String.format("%s : %s = %s", message, propertyPath, invalidValue);
      joiner.add(detailMessage);
    }

    // 모든 제약 조건 위반을 하나의 로그 라인으로 출력
    log.error("[ERROR] 제약 조건 위반 : {}", joiner);
  }

  // 요청 데이터 타입 변환 실패
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    logMethodArgumentTypeMismatch(ex);
    ExceptionResponse response = ExceptionResponse.of(ex);
    return ResponseEntity.badRequest().body(response);
  }

  private void logMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String name = ex.getName();
    String type = ex.getRequiredType().getSimpleName();
    Object value = ex.getValue();
    String message = String.format("%s = %s -> %s Type", name, value, type);
    log.error("[ERROR] 타입 변환 실패 : {}", message);
  }

  // Before Controller
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    Throwable cause = ex.getCause();
    String message;

    if (cause instanceof InvalidFormatException) {
      logInvalidFormat((InvalidFormatException) cause);
    } else if (cause instanceof UnrecognizedPropertyException) {
      logUnrecognizedProperty((UnrecognizedPropertyException) cause);
    } else if (cause instanceof JsonParseException) {
      logJsonParse((JsonParseException) cause);
    } else if (cause instanceof MismatchedInputException) {
      logMismatchedInput((MismatchedInputException) cause);
    } else {
      log.error("[ERROR] 알 수 없는 HTTP 메시지 읽기 오류: {}", ex.getMessage());
    }

    return ResponseEntity.badRequest()
        .body(
            ExceptionResponse.of(
                METHOD_ARGUMENT_NOT_VALID.getStatus().value(),
                METHOD_ARGUMENT_NOT_VALID.getCode(),
                "올바르지 않은 요청입니다."));
  }

  private void logInvalidFormat(InvalidFormatException ex) {
    String fieldName = getFieldNameFromPath(ex.getPath());
    Object invalidValue = ex.getValue();
    String targetType = ex.getTargetType() != null ? ex.getTargetType().getSimpleName() : "unknown";
    String message =
        String.format("'%s = %s' 은(는) '%s' 타입으로 변환할 수 없습니다.", fieldName, invalidValue, targetType);

    log.error("[ERROR] 타입 변환 실패: {}", message);
  }

  private void logUnrecognizedProperty(UnrecognizedPropertyException ex) {
    String propertyName = ex.getPropertyName();
    String className = ex.getReferringClass().getSimpleName();

    String detailMessage = String.format("'%s' 은(는) '%s' 클래스에 존재하지 않습니다", propertyName, className);

    log.error("[ERROR] 알 수 없는 필드: {}", detailMessage);
  }

  private void logJsonParse(JsonParseException ex) {
    String detailMessage = String.format("JSON 파싱 오류: %s", ex.getOriginalMessage());
    log.error("[ERROR] JSON 구문 오류: {} (위치: {})", detailMessage, ex.getLocation());
  }

  private void logMismatchedInput(MismatchedInputException ex) {
    String fieldName = getFieldNameFromPath(ex.getPath());
    String targetType = ex.getTargetType() != null ? ex.getTargetType().getSimpleName() : "unknown";

    String detailMessage = String.format("'%s' 이(가) '%s'과(와) 일치하지 않습니다", fieldName, targetType);

    log.error("[ERROR] 입력 타입 불일치: {}", detailMessage);
  }

  private String getFieldNameFromPath(List<Reference> path) {
    if (path == null || path.isEmpty()) {
      return "unknown";
    }

    StringBuilder fieldPath = new StringBuilder();
    for (JsonMappingException.Reference reference : path) {
      if (fieldPath.length() > 0) {
        fieldPath.append(".");
      }
      if (reference.getFieldName() != null) {
        fieldPath.append(reference.getFieldName());
      } else {
        fieldPath.append("[").append(reference.getIndex()).append("]");
      }
    }

    return fieldPath.toString();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleAllException(Exception ex) {
    log.error("[ERROR] 정의되지 않은 예외 발생: {}", ex.getMessage());
    ex.printStackTrace();
    return ResponseEntity.badRequest().body(ExceptionResponse.of(ex));
  }
}
