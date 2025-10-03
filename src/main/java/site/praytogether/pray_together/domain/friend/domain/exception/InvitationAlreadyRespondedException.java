package site.praytogether.pray_together.domain.friend.domain.exception;

import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationStatus;
import site.praytogether.pray_together.exception.ExceptionField;

public class InvitationAlreadyRespondedException extends FriendException {

  public InvitationAlreadyRespondedException(Long invitationId, FriendInvitationStatus currentStatus) {
    this(ExceptionField.builder()
        .add("invitationId", invitationId)
        .add("currentStatus", currentStatus.name())
        .build());
  }

  protected InvitationAlreadyRespondedException(ExceptionField fields) {
    super(FriendExceptionSpec.INVITATION_ALREADY_RESPONDED, fields);
  }

  @Override
  public String getClientMessage() {
    return "이미 처리된 친구 초대입니다.";
  }
}