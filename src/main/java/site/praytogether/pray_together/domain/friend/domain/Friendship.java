package site.praytogether.pray_together.domain.friend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Table(name = "friendship")
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
}
