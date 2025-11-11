package site.praytogether.pray_together.domain.fcm_token.model;

import jakarta.persistence.Column;
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
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import site.praytogether.pray_together.domain.base.BaseEntity;
import site.praytogether.pray_together.domain.fcm_token.dto.FcmTokenRegisterRequest;
import site.praytogether.pray_together.domain.member.model.Member;

@Entity
@Getter
@Builder
@Table(name = "fcm_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fcm_token_seq_generator")
  @SequenceGenerator(
      name = "fcm_token_seq_generator",
      sequenceName = "fcm_token_seq",
      allocationSize = 100)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Member member;

  @Column(name = "token", nullable = false, length = 512)
  private String token;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  public static FcmToken create(FcmTokenRegisterRequest request, Member member) {
    return FcmToken.builder().token(request.getFcmToken()).member(member).isActive(true).build();
  }
}
