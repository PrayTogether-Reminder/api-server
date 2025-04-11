package site.praytogether.pray_together.domain.invitation.exception;

import static site.praytogether.pray_together.exception.spec.InvitationExceptionSpec.INVITATION_NOT_FOUND;

import site.praytogether.pray_together.exception.ExceptionField;

public class InvitationNotFoundException extends InvitationException {

  public InvitationNotFoundException(Long inviteeId, Long invitationId) {
    this(
        ExceptionField.builder()
            .add("invitee Id", inviteeId)
            .add("invitation Id", invitationId)
            .build());
  }

  protected InvitationNotFoundException(ExceptionField fields) {
    super(INVITATION_NOT_FOUND, fields);
  }

  @Override
  public String getClientMessage() {
    return "방 초대장을 찾을 수 없습니다.";
  }
}
