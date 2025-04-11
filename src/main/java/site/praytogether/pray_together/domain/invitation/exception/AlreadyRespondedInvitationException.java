package site.praytogether.pray_together.domain.invitation.exception;

import static site.praytogether.pray_together.exception.spec.InvitationExceptionSpec.ALREADY_RESPONDED_INVITATION;

import site.praytogether.pray_together.domain.invitation.model.InvitationStatus;
import site.praytogether.pray_together.exception.ExceptionField;

public class AlreadyRespondedInvitationException extends InvitationException {

  public AlreadyRespondedInvitationException(Long invitationId, InvitationStatus status) {
    this(ExceptionField.builder().add("invitation Id", invitationId).add("status", status).build());
  }

  protected AlreadyRespondedInvitationException(ExceptionField fields) {
    super(ALREADY_RESPONDED_INVITATION, fields);
  }

  @Override
  public String getClientMessage() {
    return "이미 응답한 초대장입니다.";
  }
}
