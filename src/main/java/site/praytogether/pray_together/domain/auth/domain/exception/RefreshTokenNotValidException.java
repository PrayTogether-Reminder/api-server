package site.praytogether.pray_together.domain.auth.domain.exception;

import static site.praytogether.pray_together.domain.auth.domain.exception.AuthExceptionSpec.REFRESH_TOKEN_NOT_VALID;

import site.praytogether.pray_together.exception.ExceptionField;

public class RefreshTokenNotValidException extends AuthException {

  public RefreshTokenNotValidException(Long memberId) {
    this(ExceptionField.builder().add("memberId", memberId).build());
  }

  protected RefreshTokenNotValidException(ExceptionField fields) {
    super(REFRESH_TOKEN_NOT_VALID, fields);
  }

  @Override
  public String getClientMessage() {
    return "다시 로그인을 해주세요.";
  }
}
