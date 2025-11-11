package site.praytogether.pray_together.domain.friend.domain.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class SelfFriendOperationException extends FriendException {

  public SelfFriendOperationException(Long memberId) {
    this(ExceptionField.builder().add("memberId", memberId).build());
  }

  protected SelfFriendOperationException(ExceptionField fields) {
    super(FriendExceptionSpec.SELF_FRIEND_OPERATION_NOT_ALLOWED, fields);
  }

  @Override
  public String getClientMessage() {
    return "자기 자신에게는 친구 작업을 수행할 수 없습니다.";
  }
}