package site.praytogether.pray_together.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import site.praytogether.pray_together.domain.auth.domain.OAuthProvider;
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

  @Column(nullable = false, length = MemberConstant.NAME_MAX_LEN)
  private String name;

  @Column(nullable = true, length = MemberConstant.PASSWORD_MAX_LEN)
  private String password;

  @Embedded
  private PhoneNumber phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = MemberConstant.OAUTH_PROVIDER_MAX_LEN)
  private OAuthProvider provider;

  @Column(nullable = true, length = MemberConstant.PROVIDER_MEMBER_ID_MAX_LEN, unique = true)
  private String providerMemberId;

  public static Member create(String name, String email, String password, PhoneNumber phoneNumber) {
    return Member.builder()
        .name(name)
        .email(email)
        .password(password)
        .phoneNumber(phoneNumber)
        .provider(OAuthProvider.LOCAL)
        .build();
  }

  public static Member createGoogleMember(String name, String email, PhoneNumber phoneNumber) {
    return Member.builder()
        .name(name)
        .email(email)
        .password(null)
        .phoneNumber(phoneNumber)
        .provider(OAuthProvider.GOOGLE)
        .build();
  }

  public static Member createAppleMember(String name, String providerMemberId) {
    return Member.builder()
        .name(name)
        .email(providerMemberId)
        .password(null)
        .phoneNumber(null)
        .provider(OAuthProvider.APPLE)
        .providerMemberId(providerMemberId)
        .build();
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updatePhoneNumber(PhoneNumber phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void updatePassword(String password) {
    this.password = password;
  }
}
