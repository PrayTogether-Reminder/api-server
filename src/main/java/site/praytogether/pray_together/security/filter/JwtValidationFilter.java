package site.praytogether.pray_together.security.filter;

import static site.praytogether.pray_together.constant.CoreConstant.JwtConstant.HTTP_HEADER_AUTHORIZATION;
import static site.praytogether.pray_together.constant.CoreConstant.JwtConstant.HTTP_HEADER_AUTH_BEARER;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;
import site.praytogether.pray_together.domain.auth.domain.PrayTogetherPrincipal;
import site.praytogether.pray_together.security.exception.JwtAuthenticationException;
import site.praytogether.pray_together.security.service.JwtService;

@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final JwtService jwtService;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = extractTokenFrom(request);

    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      jwtService.isValid(token);
      setAuthentication(token);
      filterChain.doFilter(request, response);
    } catch (JwtException e) {
      logger.error("JWT 예외 발생 : {}", e.getMessage());
      authenticationEntryPoint.commence(request, response, new JwtAuthenticationException(e));
    }
  }

  private String extractTokenFrom(HttpServletRequest request) {
    String value = request.getHeader(HTTP_HEADER_AUTHORIZATION);
    if (value == null || !value.contains(HTTP_HEADER_AUTH_BEARER)) {
      return null;
    }
    return value.substring(HTTP_HEADER_AUTH_BEARER.length());
  }

  private void setAuthentication(String token) {
    Authentication authentication = getAuthentication(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    logger.info("인증 정보 구성 완료 : {}", authentication.getPrincipal());
  }

  private Authentication getAuthentication(String token) {
    PrayTogetherPrincipal principal = jwtService.extractPrincipal(token);
    return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
  }
}
