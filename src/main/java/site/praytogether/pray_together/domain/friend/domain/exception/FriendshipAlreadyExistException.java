package site.praytogether.pray_together.domain.friend.domain.exception;

import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

public class FriendshipAlreadyExistException extends FriendException {

  public FriendshipAlreadyExistException(Long inviterId, Long inviteeId){
    this(ExceptionField.builder().add("inviterId", inviterId).add("inviteeId", inviteeId).build());
  }

  protected FriendshipAlreadyExistException(ExceptionField fields) {
    super(FriendExceptionSpec.FRIENDSHIP_ALREADY_EXIST
        ,fields);
  }

  @Override
  public String getClientMessage() {
    return "이미 친구 입니다.";
  }
}
