package site.praytogether.pray_together.domain.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import site.praytogether.pray_together.domain.base.BaseEntity;
import site.praytogether.pray_together.domain.member.model.Member;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false, updatable = false, unique = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Member member;

  @Column(name = "token", nullable = false, length = 512)
  private String token;

  @Column(name = "expired_time", nullable = false, columnDefinition = "TIMESTAMP(3) WITH TIME ZONE")
  private Instant expiredTime;

  public static RefreshToken create(Member member, String token, Instant expiredTime) {
    return RefreshToken.builder().member(member).token(token).expiredTime(expiredTime).build();
  }

  public void updateToken(String token, Instant expiredTime) {
    this.token = token;
    this.expiredTime = expiredTime;
  }
}
