package site.praytogether.pray_together.domain.invitation.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.invitation.model.InvitationInfo;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvitationInfoListResponse {
  private List<InvitationInfo> invitations;

  public static InvitationInfoListResponse from(List<InvitationInfo> invitationInfos) {
    return new InvitationInfoListResponse(invitationInfos);
  }
}
