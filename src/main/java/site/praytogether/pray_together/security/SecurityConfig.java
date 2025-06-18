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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import site.praytogether.pray_together.domain.auth.service.RefreshTokenService;
import site.praytogether.pray_together.security.filter.JwtAuthFilter;
import site.praytogether.pray_together.security.filter.JwtLogoutFilter;
import site.praytogether.pray_together.security.filter.JwtValidationFilter;
import site.praytogether.pray_together.security.service.JwtService;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

  private final ObjectMapper objectMapper;
  private final RefreshTokenService refreshTokenService;
  private final JwtService jwtService;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    return http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/api/v1/auth/**", "/error", "/health")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(new JwtLogoutFilter(refreshTokenService), LogoutFilter.class)
        .addFilterBefore(
            new JwtAuthFilter(authenticationManager, objectMapper, refreshTokenService, jwtService),
            JwtLogoutFilter.class)
        .addFilterBefore(
            new JwtValidationFilter(jwtService, authenticationEntryPoint), JwtAuthFilter.class)
        .exceptionHandling(
            exceptions -> exceptions.authenticationEntryPoint(authenticationEntryPoint))
        .csrf(AbstractHttpConfigurer::disable)
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
