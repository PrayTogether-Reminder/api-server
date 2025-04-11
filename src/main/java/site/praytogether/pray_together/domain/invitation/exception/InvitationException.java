package site.praytogether.pray_together.domain.invitation.exception;

import site.praytogether.pray_together.exception.BaseException;
import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

public abstract class InvitationException extends BaseException {

  protected InvitationException(ExceptionSpec spec, ExceptionField fields) {
    super(spec, fields);
  }
}
