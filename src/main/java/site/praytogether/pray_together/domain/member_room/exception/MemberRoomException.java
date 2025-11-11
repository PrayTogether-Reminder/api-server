package site.praytogether.pray_together.domain.member_room.exception;

import site.praytogether.pray_together.exception.BaseException;
import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

public abstract class MemberRoomException extends BaseException {

  protected MemberRoomException(ExceptionSpec spec, ExceptionField fields) {
    super(spec, fields);
  }
}
