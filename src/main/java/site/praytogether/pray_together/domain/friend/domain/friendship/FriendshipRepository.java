package site.praytogether.pray_together.domain.friend.domain.friendship;

public interface FriendshipRepository {

  boolean isExist(Long inviter, Long invitee);
}
