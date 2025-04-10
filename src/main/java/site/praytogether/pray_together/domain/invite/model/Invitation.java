package site.praytogether.pray_together.domain.invite.model;

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
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.room.model.Room;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "invitation")
@SequenceGenerator(
    name = "INVITATION_SEQ_GENERATOR",
    sequenceName = "INVITATION_SEQ",
    initialValue = 1,
    allocationSize = 50)
public class Invitation extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INVITATION_SEQ_GENERATOR")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Room room;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invitee_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Member member;

  @Column(name = "inviter_name", nullable = false)
  private String inviterName;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private InvitationStatus status;

  @Column(name = "response_time", nullable = false)
  private Instant responseTime;

  public static Invitation create(Room room, Member member, String inviterName) {
    return Invitation.builder()
        .room(room)
        .member(member)
        .inviterName(inviterName)
        .status(InvitationStatus.PENDING)
        .build();
  }

  public void accept() {
    this.status = InvitationStatus.ACCEPTED;
    this.responseTime = Instant.now();
  }

  public void reject() {
    this.status = InvitationStatus.REJECTED;
    this.responseTime = Instant.now();
  }
}
