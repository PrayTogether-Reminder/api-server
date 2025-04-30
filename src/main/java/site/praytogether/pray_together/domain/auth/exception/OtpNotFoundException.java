package site.praytogether.pray_together.domain.auth.exception;

import static site.praytogether.pray_together.domain.auth.exception.AuthExceptionSpec.OTP_TEMPLATE_LOAD_FAIL;

import site.praytogether.pray_together.exception.ExceptionField;

public class OtpNotFoundException extends AuthException {

  public OtpNotFoundException(String email) {
    this(ExceptionField.builder().add("email", email).build());
  }

  protected OtpNotFoundException(ExceptionField fields) {
    super(OTP_TEMPLATE_LOAD_FAIL, fields);
  }

  @Override
  public String getClientMessage() {
    return "등록되지 않은 OTP 입니다.";
  }
}
