package site.praytogether.pray_together.domain.auth.exception;

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
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}
