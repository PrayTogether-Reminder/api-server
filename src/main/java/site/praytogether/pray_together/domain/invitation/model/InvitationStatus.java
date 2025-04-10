package site.praytogether.pray_together.domain.invitation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InvitationStatus {
  PENDING("대기"),
  ACCEPTED("수락"),
  REJECTED("거절"),
  ;

  private final String koreanName;
}
