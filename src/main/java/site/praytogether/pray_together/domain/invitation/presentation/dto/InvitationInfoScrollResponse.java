package site.praytogether.pray_together.domain.invitation.presentation.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.invitation.domain.InvitationInfo;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvitationInfoScrollResponse {
  private List<InvitationInfo> invitations;

  public static InvitationInfoScrollResponse from(List<InvitationInfo> invitationInfos) {
    return new InvitationInfoScrollResponse(invitationInfos);
  }
}
