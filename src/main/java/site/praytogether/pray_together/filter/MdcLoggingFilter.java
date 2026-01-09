package site.praytogether.pray_together.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 인증 전에 실행되는 MDC 필터.
 * requestId, clientIp를 설정하고 요청 완료 후 MDC를 정리합니다.
 */
@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

  private static final String REQUEST_ID_KEY = "requestId";
  private static final String CLIENT_IP_KEY = "clientIp";
  private static final String REQUEST_URI_KEY = "requestUri";

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String requestId = UUID.randomUUID().toString();
      MDC.put(REQUEST_ID_KEY, requestId);

      String clientIp = getClientIp(request);
      MDC.put(CLIENT_IP_KEY, clientIp);

      String requestUri = request.getRequestURI();
      MDC.put(REQUEST_URI_KEY, requestUri);

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
