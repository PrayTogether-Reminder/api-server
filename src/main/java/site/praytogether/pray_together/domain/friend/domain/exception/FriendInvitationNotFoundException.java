package site.praytogether.pray_together.domain.friend.domain.exception;

import site.praytogether.pray_together.exception.ExceptionField;

public class FriendInvitationNotFoundException extends FriendException{

  public FriendInvitationNotFoundException(Long receiverId,Long invitationId) {
    this(ExceptionField.builder().add("receiverId",receiverId).add("invitationId", invitationId).build());
  }

  protected FriendInvitationNotFoundException(ExceptionField fields) {
    super(FriendExceptionSpec.FRIEND_INVITATION_NOF_FOUND, fields);
  }

  @Override
  public String getClientMessage() {
    return "친구 초대장을 찾을 수 없습니다.";
  }
}
