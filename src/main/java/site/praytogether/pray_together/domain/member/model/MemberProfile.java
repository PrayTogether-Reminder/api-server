package site.praytogether.pray_together.domain.member.model;

import org.springframework.beans.factory.annotation.Value;
import site.praytogether.pray_together.domain.auth.domain.OAuthProvider;

public interface MemberProfile {
  Long getId();

  String getName();

  String getEmail();

  PhoneNumber getPhoneNumber();

  OAuthProvider getProvider();
}
