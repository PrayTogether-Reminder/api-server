package site.praytogether.pray_together.domain.member.model;

import org.springframework.beans.factory.annotation.Value;

public interface MemberProfile {
  Long getId();

  String getName();

  String getEmail();

  @Value("#{target.phoneNumber.value}") // 전체 Entity를 load 함.
  String getPhoneNumber();
}
