package site.praytogether.pray_together.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import site.praytogether.pray_together.domain.auth.domain.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.auth.domain.RefreshTokenService;

@RequiredArgsConstructor
public class JwtLogoutFilter extends OncePerRequestFilter {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final String LOGOUT_URL = "/api/v1/auth/logout";
  private final RefreshTokenService refreshTokenService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (shouldNotFilter(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    logger.info("로그아웃 {}", ((PrayTogetherPrincipal) authentication.getPrincipal()).getId());
    logger.info("로그아웃 인증 정보 = {}", authentication);
    if (authentication == null) return;
    PrayTogetherPrincipal principal = (PrayTogetherPrincipal) authentication.getPrincipal();
    refreshTokenService.delete(principal.getId());

    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getServletPath().equals(LOGOUT_URL);
  }
}
