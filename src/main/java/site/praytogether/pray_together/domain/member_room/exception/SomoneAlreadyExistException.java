package site.praytogether.pray_together.domain.member_room.exception;

import java.util.List;
import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;
import site.praytogether.pray_together.exception.spec.MemberRoomExceptionSpec;

public class SomoneAlreadyExistException extends MemberRoomException {


  public SomoneAlreadyExistException(List<Long> memberIds, Long roomId) {
    this(ExceptionField.builder().add("roomId", roomId).add("memberIds",memberIds).build());
  }

  protected SomoneAlreadyExistException(ExceptionField fields) {
    super(MemberRoomExceptionSpec.SOMONE_ALREADY_EXIST, fields);
  }

  @Override
  public String getClientMessage() {
    return "이미 방에 참가한 회원이 있습니다.";
  }
}
