package site.praytogether.pray_together.domain.auth.exception;

import site.praytogether.pray_together.exception.BaseException;
import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

public abstract class AuthException extends BaseException {

  protected AuthException(ExceptionSpec spec, ExceptionField fields) {
    super(spec, fields);
  }
}
