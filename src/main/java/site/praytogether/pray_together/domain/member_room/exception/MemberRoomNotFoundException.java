package site.praytogether.pray_together.domain.member_room.exception;

import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.MemberRoomExceptionSpec;

public class MemberRoomNotFoundException extends MemberRoomException {

  public MemberRoomNotFoundException(Long memberId, Long roomId) {
    this(ExceptionField.builder().add("memberId", memberId).add("roomId", roomId).build());
  }

  protected MemberRoomNotFoundException(ExceptionField fields) {
    super(MemberRoomExceptionSpec.MEMBER_ROOM_NOT_FOUND, fields);
  }

  @Override
  public String getClientMessage() {
    return "회원님이 속해있는 방을 찾지 못 했습니다.";
  }
}
