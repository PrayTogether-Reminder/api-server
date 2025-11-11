package site.praytogether.pray_together.domain.member.expcetion;

import site.praytogether.pray_together.exception.BaseException;
import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

public abstract class MemberException extends BaseException {

  protected MemberException(ExceptionSpec spec, ExceptionField field) {
    super(spec, field);
  }
}
