package site.praytogether.pray_together.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

  @Bean
  public AuditorAware<Long> auditorAware() {
    return () ->
        Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .filter(principal -> principal instanceof PrayTogetherPrincipal)
            .map(PrayTogetherPrincipal.class::cast)
            .map(PrayTogetherPrincipal::getId);
  }
}
