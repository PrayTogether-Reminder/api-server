package site.praytogether.pray_together.domain.friend.domain.friendship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.friend.domain.exception.FriendshipAlreadyExistException;
import site.praytogether.pray_together.domain.friend.domain.exception.FriendshipNotFoundException;
import site.praytogether.pray_together.domain.friend.domain.exception.SelfFriendOperationException;
import site.praytogether.pray_together.domain.member.model.Member;

@Component
@RequiredArgsConstructor
public class FriendshipService {
  private final FriendshipRepository friendshipRepository;

  private boolean isFriendshipExists(Long memberId1, Long memberId2) {
    Long smallerId = Math.min(memberId1, memberId2);
    Long biggerId = Math.max(memberId1, memberId2);
    return friendshipRepository.isExist(smallerId, biggerId);
  }

  public void ensureNotSameMember(Long memberId1, Long memberId2) {
    if (memberId1.equals(memberId2)) {
      throw new SelfFriendOperationException(memberId1);
    }
  }

  public void ensureAlreadyNotFriends(Member inviter, Member invitee) {
    if (isFriendshipExists(inviter.getId(), invitee.getId())) {
      throw new FriendshipAlreadyExistException(inviter.getId(), invitee.getId());
    }
  }

  public void createFriendship(Member sender, Member receiver) {
    Friendship friendship = Friendship.create(sender, receiver);
    friendshipRepository.save(friendship);
  }

  // 멱등성 보장: 이미 친구면 조용히 무시
  public void createFriendshipIfNotExists(Member sender, Member receiver) {
    if (!isFriendshipExists(sender.getId(), receiver.getId())) {
      Friendship friendship = Friendship.create(sender, receiver);
      friendshipRepository.save(friendship);
    }
  }

  public List<Friendship> fetchListBy(Member member) {
    return friendshipRepository.getFriendshipList(member.getId());
  }

  public void delete(Member member1, Member member2) {
    Long smallerId = Math.min(member1.getId(), member2.getId());
    Long biggerId = Math.max(member1.getId(), member2.getId());

    Friendship friendship = friendshipRepository.findByMemberIds(smallerId, biggerId)
        .orElseThrow(() -> new FriendshipNotFoundException(smallerId, biggerId));

    friendshipRepository.delete(friendship);
  }
}
