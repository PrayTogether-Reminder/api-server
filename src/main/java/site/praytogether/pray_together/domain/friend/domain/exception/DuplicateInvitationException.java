package site.praytogether.pray_together.domain.friend.domain.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class DuplicateInvitationException extends FriendException {

  public DuplicateInvitationException(Long senderId, Long receiverId){
    this(ExceptionField.builder().add("senderId", senderId).add("receiverId", receiverId).build());
  }

  protected DuplicateInvitationException(ExceptionField fields) {
    super(FriendExceptionSpec.DUPLICATE_INVITATION, fields);
  }

  @Override
  public String getClientMessage() {
    return "이미 친구 요청을 보낸 상태입니다.";
  }
}