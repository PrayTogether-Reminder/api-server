package site.praytogether.pray_together.domain.auth.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class RefreshTokenExpiredException extends AuthException {

  public RefreshTokenExpiredException() {
    this(ExceptionField.builder().build());
  }

  public RefreshTokenExpiredException(Long memberId) {
    this(ExceptionField.builder().add("memberId", memberId).build());
  }

  protected RefreshTokenExpiredException(ExceptionField fields) {
    super(AuthExceptionSpec.REFRESH_TOKEN_EXPIRED, fields);
  }

  @Override
  public String getClientMessage() {
    return "다시 로그인을 해주세요.";
  }
}
