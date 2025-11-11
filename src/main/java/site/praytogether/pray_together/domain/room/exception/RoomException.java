package site.praytogether.pray_together.domain.room.exception;

import site.praytogether.pray_together.exception.BaseException;
import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

public abstract class RoomException extends BaseException {

  protected RoomException(ExceptionSpec spec, ExceptionField fields) {
    super(spec, fields);
  }
}
