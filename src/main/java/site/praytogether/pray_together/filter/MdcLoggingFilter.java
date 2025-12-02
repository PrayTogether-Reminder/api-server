package site.praytogether.pray_together.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import site.praytogether.pray_together.domain.auth.domain.PrayTogetherPrincipal;

@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

  private static final String MEMBER_ID_KEY = "memberId";
  private static final String REQUEST_ID_KEY = "requestId";

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Generate unique request ID
      String requestId = UUID.randomUUID().toString();
      MDC.put(REQUEST_ID_KEY, requestId);

      // Extract memberId from SecurityContext if authenticated
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null
          && authentication.isAuthenticated()
          && authentication.getPrincipal() instanceof PrayTogetherPrincipal principal) {
        MDC.put(MEMBER_ID_KEY, String.valueOf(principal.getId()));
      }

      filterChain.doFilter(request, response);
    } finally {
      // Always clear MDC to prevent memory leaks
      MDC.clear();
    }
  }
}
