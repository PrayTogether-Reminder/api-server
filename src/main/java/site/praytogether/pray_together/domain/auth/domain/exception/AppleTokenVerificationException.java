package site.praytogether.pray_together.domain.auth.domain.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class AppleTokenVerificationException extends AuthException {

  public AppleTokenVerificationException(String reason) {
    super(AuthExceptionSpec.APPLE_TOKEN_VERIFICATION_FAILED,
        ExceptionField.builder().add("reason", reason).build());
  }

  @Override
  public String getClientMessage() {
    return "Apple 인증에 실패했습니다. 다시 시도해 주세요.";
  }
}
