package site.praytogether.pray_together.domain.friend.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.friend.domain.exception.FriendshipAlreadyExistException;
import site.praytogether.pray_together.domain.member.model.Member;

@Component
@RequiredArgsConstructor
public class FriendshipService {
  private final FriendshipRepository friendshipRepository;

  public void ensureAlreadyNotFriends(Member inviter, Member invitee) {
    Long memberId1 = Math.min(inviter.getId(), invitee.getId());
    Long memberId2 = Math.max(inviter.getId(), invitee.getId());

    boolean exist = friendshipRepository.isExist(memberId1,memberId2);
    if (exist) {
      throw new FriendshipAlreadyExistException(inviter.getId(), invitee.getId());
    }
  }
}
