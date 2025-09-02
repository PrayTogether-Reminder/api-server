package site.praytogether.pray_together.domain.friend.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendInvitationService {
  private final FriendInvitationRepository friendInvitationRepository;
}
