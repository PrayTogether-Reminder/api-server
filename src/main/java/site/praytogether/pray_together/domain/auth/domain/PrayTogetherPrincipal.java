package site.praytogether.pray_together.domain.auth.domain;

import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
@Getter
public class PrayTogetherPrincipal implements UserDetails {

  private final Long id;
  private final String email;
  private final String username; // equals with email
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return (Collection<? extends GrantedAuthority>) Map.of();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
