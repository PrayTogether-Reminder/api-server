package site.praytogether.pray_together.domain.auth.model;

import io.jsonwebtoken.lang.Collections;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@AllArgsConstructor
@Getter
public class PrayTogetherPrincipal implements UserDetails {

  private final Long id;
  private final String email;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
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
