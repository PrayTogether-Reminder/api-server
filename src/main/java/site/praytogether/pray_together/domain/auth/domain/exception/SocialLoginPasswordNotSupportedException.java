package site.praytogether.pray_together.domain.auth.domain.exception;

import static site.praytogether.pray_together.domain.auth.domain.exception.AuthExceptionSpec.SOCIAL_LOGIN_ONLY;

import site.praytogether.pray_together.exception.ExceptionField;

public class SocialLoginPasswordNotSupportedException extends AuthException {

  public SocialLoginPasswordNotSupportedException(Long memberId) {
    this(ExceptionField.builder().add("memberId", memberId).build());
  }

  protected SocialLoginPasswordNotSupportedException(ExceptionField fields) {
    super(SOCIAL_LOGIN_ONLY, fields);
  }

  @Override
  public String getClientMessage() {
    return "소셜 로그인 계정은 비밀번호 기능을 사용할 수 없습니다.";
  }
}
