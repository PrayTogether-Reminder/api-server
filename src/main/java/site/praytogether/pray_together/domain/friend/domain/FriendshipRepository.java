package site.praytogether.pray_together.domain.friend.domain;

public interface FriendshipRepository {

  boolean isExist(Long inviter, Long invitee);
}
