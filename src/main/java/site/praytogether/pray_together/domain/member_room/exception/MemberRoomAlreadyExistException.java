package site.praytogether.pray_together.domain.member_room.exception;

import static site.praytogether.pray_together.exception.spec.MemberRoomExceptionSpec.MEMBER_ROOM_ALREADY_EXIST;

import site.praytogether.pray_together.exception.ExceptionField;

public class MemberRoomAlreadyExistException extends MemberRoomException {

  public MemberRoomAlreadyExistException(Long memberId, Long roomId) {
    this(ExceptionField.builder().add("memberId", memberId).add("roomId", roomId).build());
  }

  protected MemberRoomAlreadyExistException(ExceptionField fields) {
    super(MEMBER_ROOM_ALREADY_EXIST, fields);
  }

  @Override
  public String getClientMessage() {
    return "이미 방에 존재하는 회원입니다.";
  }
}
