package site.praytogether.pray_together.domain.auth.domain.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class OtpSendFailException extends AuthException {

  public OtpSendFailException(String email) {
    this(ExceptionField.builder().add("email", email).build());
  }

  protected OtpSendFailException(ExceptionField fields) {
    super(AuthExceptionSpec.OTP_SEND_FAIL, fields);
  }

  @Override
  public String getClientMessage() {
    return "OTP 전송을 실패했습니다.";
  }
}
