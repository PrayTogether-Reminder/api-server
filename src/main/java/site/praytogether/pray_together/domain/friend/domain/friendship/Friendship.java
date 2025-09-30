package site.praytogether.pray_together.domain.friend.domain.friendship;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import site.praytogether.pray_together.domain.base.BaseEntity;
import site.praytogether.pray_together.domain.member.model.Member;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "friendship",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id_1", "member_id_2"})
    }
)
@SequenceGenerator(
    name = "FRIENDSHIP_SEQ_GENERATOR",
    sequenceName = "FRIENDSHIP_SEQ",
    initialValue = 1,
    allocationSize = 50)
public class Friendship extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FRIENDSHIP_SEQ_GENERATOR")
  private Long id;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id_1",nullable = false,updatable = false)
  private Member member1;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id_2",nullable = false,updatable = false)
  private Member member2;

  public static Friendship create(Member sender, Member receiver) {
    Member member1;
    Member member2;
    if(sender.getId() < receiver.getId()) { // small id -> member1 , big id -> member2
      member1 = sender;
      member2 = receiver;
    } else {
      member1 = receiver;
      member2 = sender;
    }

    return Friendship.builder()
        .member1(member1)
        .member2(member2)
        .build();
  }

  public Member getFriendBy(Member me) {
    if(Objects.equals(me.getId(),member1.getId()) ) {
      return member2;
    }
    return member1;
  }
}
