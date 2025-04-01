package site.praytogether.pray_together.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import site.praytogether.pray_together.domain.auth.cache.RefreshTokenCache;
import site.praytogether.pray_together.security.filter.JwtAuthFilter;
import site.praytogether.pray_together.security.service.JwtService;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

  private final ObjectMapper objectMapper;
  private final RefreshTokenCache refreshTokenCache;
  private final JwtService jwtService;

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    return http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/api/v1/auth/**", "/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(
            new JwtAuthFilter(authenticationManager, objectMapper, refreshTokenCache, jwtService),
            LogoutFilter.class)
        .csrf(AbstractHttpConfigurer::disable)
        .anonymous(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .build();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
