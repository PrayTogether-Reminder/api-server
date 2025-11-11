package site.praytogether.pray_together.domain.room.exception;

import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.RoomExceptionSpec;

public class RoomNotFoundException extends RoomException {

  public RoomNotFoundException(Long roomId) {
    this(ExceptionField.builder().add("roomId", roomId).build());
  }

  protected RoomNotFoundException(ExceptionField fields) {
    super(RoomExceptionSpec.ROOM_NOT_FOUND, fields);
  }

  @Override
  public String getClientMessage() {
    return "방을 찾을 수 없습니다.";
  }
}
