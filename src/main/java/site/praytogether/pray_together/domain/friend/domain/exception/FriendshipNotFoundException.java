package site.praytogether.pray_together.domain.friend.domain.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class FriendshipNotFoundException extends FriendException {

  public FriendshipNotFoundException(Long memberId1, Long memberId2) {
    this(ExceptionField.builder().add("memberId1", memberId1).add("memberId2", memberId2).build());
  }

  protected FriendshipNotFoundException(ExceptionField fields) {
    super(FriendExceptionSpec.FRIENDSHIP_NOT_FOUND, fields);
  }

  @Override
  public String getClientMessage() {
    return "존재하지 않는 친구 관계입니다.";
  }
}