package site.praytogether.pray_together.domain.friend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendInvitationStatus {
  PENDING("대기"),
  ACCEPTED("수락"),
  REJECTED("거절"),
  ;

  private final String koreanName;
}
