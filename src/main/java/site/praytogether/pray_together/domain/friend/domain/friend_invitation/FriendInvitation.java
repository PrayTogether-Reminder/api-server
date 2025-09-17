package site.praytogether.pray_together.domain.friend.domain.friend_invitation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import site.praytogether.pray_together.domain.base.BaseEntity;
import site.praytogether.pray_together.domain.friend.domain.exception.InvitationAlreadyRespondedException;
import site.praytogether.pray_together.domain.member.model.Member;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "friend_request")
@SequenceGenerator(
    name = "FRIEND_REQUEST_SEQ_GENERATOR",
    sequenceName = "FRIEND_REQUEST_SEQ",
    initialValue = 1,
    allocationSize = 50)
public class FriendInvitation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FRIEND_REQUEST_SEQ_GENERATOR")
  private Long id;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id",nullable = false,updatable = false)
  private Member sender;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id",nullable = false,updatable = false)
  private Member receiver;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FriendInvitationStatus status;

  @Column(name = "response_time")
  private Instant responseTime;

  public static FriendInvitation create(Member inviter, Member invitee) {
    return FriendInvitation.builder()
        .sender(inviter)
        .receiver(invitee)
        .status(FriendInvitationStatus.PENDING)
        .responseTime(Instant.now())
        .build();
  }

  public void updateStatus(FriendInvitationStatus status) {
    if(this.status != FriendInvitationStatus.PENDING) {
      throw new InvitationAlreadyRespondedException(this.id, this.status);
    }
    this.status = status;
    this.responseTime = Instant.now();
  }
}
