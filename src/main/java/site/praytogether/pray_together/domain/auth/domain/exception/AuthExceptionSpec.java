package site.praytogether.pray_together.domain.auth.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionSpec implements ExceptionSpec {
  OTP_SEND_FAIL(HttpStatus.BAD_REQUEST, "AUTH-001", "회원가입 OTP 전송을 실패했습니다."),
  OTP_TEMPLATE_LOAD_FAIL(
      HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-002", "회원가입 OTP 템플릿을 불러오는데 실패했습니다."),
  INCORRECT_EMAIL_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH-003", "이메일 또는 비밀번호가 올바르지 않습니다."),
  UNKNOWN_AUTHENTICATION_FAILURE(
      HttpStatus.UNAUTHORIZED, "AUTH-004", "Auth Entry Point 에서 알 수 없는 이유로 인증에 실패했습니다."),
  ACCESS_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "AUTH-005", "JWT(accessToken) 인증에 실패했습니다."),
  ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-006", "JWT(accessToken)가 만료되었습니다."),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-007", "RefreshToken을 찾을 수 없습니다."),
  REFRESH_TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "AUTH-008", "RefreshToken이 유효하지 않습니다."),
  OTP_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH-009", "email에 해당하는 OTP가 없습니다."),
  AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, "AUTH-010", "인증되지 않은 요청입니다."),
  REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-011", "RefreshToken이 만료되었습니다."),
  ;

  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
