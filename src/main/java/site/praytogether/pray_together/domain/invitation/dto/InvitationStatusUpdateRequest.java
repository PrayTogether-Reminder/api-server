package site.praytogether.pray_together.domain.invitation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import site.praytogether.pray_together.domain.invitation.model.InvitationStatus;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvitationStatusUpdateRequest {
  private InvitationStatus status;
}
