package site.praytogether.pray_together.domain.auth.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class RefreshTokenNotFoundException extends AuthException {

  public RefreshTokenNotFoundException() {
    this(ExceptionField.builder().build());
  }

  public RefreshTokenNotFoundException(Long memberId) {
    this(ExceptionField.builder().add("memberId", memberId).build());
  }

  protected RefreshTokenNotFoundException(ExceptionField fields) {
    super(AuthExceptionSpec.REFRESH_TOKEN_NOT_FOUND, fields);
  }

  @Override
  public String getClientMessage() {
    return "다시 로그인을 해주세요.";
  }
}
