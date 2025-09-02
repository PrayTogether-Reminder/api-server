package site.praytogether.pray_together.domain.friend.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.friend.domain.FriendInvitationService;
import site.praytogether.pray_together.domain.friend.domain.FriendshipService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FriendApplicationService {
  private final FriendshipService friendshipService;
  private final FriendInvitationService friendInvitationService;

  public MessageResponse inviteFriend(Long inviteeId, Long inviterId) {
    return null;
  }
}
