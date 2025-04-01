package site.praytogether.pray_together.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.praytogether.pray_together.constant.CoreConstant.MemberConstant;
import site.praytogether.pray_together.domain.base.BaseEntity;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "member")
@SequenceGenerator(
    name = "MEMBER_SEQ_GENERATOR",
    sequenceName = "MEMBER_SEQ",
    initialValue = 1,
    allocationSize = 50)
public class Member extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
  private Long id;

  @Column(nullable = false, length = MemberConstant.EMAIL_MAX_LEN, unique = true)
  private String email;

  @Column(nullable = false, length = MemberConstant.NAME_MAX_LEN, unique = true)
  private String name;

  @Column(nullable = false, length = MemberConstant.PASSWORD_MAX_LEN)
  private String password;

  public static Member create(String name, String email, String password) {
    return Member.builder().name(name).email(email).password(password).build();
  }
}
