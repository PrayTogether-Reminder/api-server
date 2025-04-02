package site.praytogether.pray_together.domain.auth.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class OtpTemplateLoadFailException extends AuthException {

  public OtpTemplateLoadFailException(String otpTemplatePath) {
    this(ExceptionField.builder().add("otp Template Path", otpTemplatePath).build());
  }

  protected OtpTemplateLoadFailException(ExceptionField fields) {
    super(AuthExceptionSpec.OTP_TEMPLATE_LOAD_FAIL, fields);
  }

  @Override
  public String getClientMessage() {
    return "서버 문제로 인해 이메일 인증 번호 전송에 실패했습니다.\n 관리자에게 문의 바랍니다.";
  }
}
