package site.praytogether.pray_together.domain.friend.domain.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class SelfInvitationException extends FriendException {

  public SelfInvitationException(Long memberId){
    this(ExceptionField.builder().add("memberId", memberId).build());
  }

  protected SelfInvitationException(ExceptionField fields) {
    super(FriendExceptionSpec.SELF_INVITATION_NOT_ALLOWED, fields);
  }

  @Override
  public String getClientMessage() {
    return "자기 자신에게는 친구 요청을 보낼 수 없습니다.";
  }
}