package site.praytogether.pray_together.domain.invitation.domain;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvitationInfo {
  private Long invitationId;
  private String inviterName;
  private String roomName;
  private String roomDescription;
  private Instant createdTime; // invitation createdTime
}
