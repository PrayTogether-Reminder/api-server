package site.praytogether.pray_together.domain.friend.domain.exception;

import site.praytogether.pray_together.exception.BaseException;
import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

public abstract class FriendException  extends BaseException {

  protected FriendException(ExceptionSpec spec,
      ExceptionField fields) {
    super(spec, fields);
  }
}
