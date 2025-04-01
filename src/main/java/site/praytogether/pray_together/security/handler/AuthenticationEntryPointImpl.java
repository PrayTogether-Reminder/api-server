package site.praytogether.pray_together.security.handler;

import static site.praytogether.pray_together.domain.auth.exception.AuthExceptionSpec.JWT_EXCEPTION;
import static site.praytogether.pray_together.domain.auth.exception.AuthExceptionSpec.UNKNOWN_AUTHENTICATION_FAILURE;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.exception.ExceptionResponse;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    logger.error("인증 실패 예외 타입={}", authException.getCause().getClass());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    if (authException.getCause() instanceof JwtException) {
      setJwtExceptionResponse(response, (JwtException) authException.getCause());
      return;
    }

    response.setStatus(UNKNOWN_AUTHENTICATION_FAILURE.getStatus().value());
    ExceptionResponse errorResponse =
        ExceptionResponse.of(
            UNKNOWN_AUTHENTICATION_FAILURE.getStatus().value(),
            UNKNOWN_AUTHENTICATION_FAILURE.getCode(),
            "알 수 없는 이유로 인증에 실패했습니다.");

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }

  private void setJwtExceptionResponse(HttpServletResponse response, JwtException e)
      throws IOException {
    String message = findJwtExceptionMessage(e);
    ExceptionResponse errorResponse =
        ExceptionResponse.of(JWT_EXCEPTION.getStatus().value(), JWT_EXCEPTION.getCode(), message);
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }

  private String findJwtExceptionMessage(JwtException jwtException) {
    if (jwtException instanceof ExpiredJwtException) {
      logger.error("토큰 기간이 만료됐습니다.");
      return "토큰 기간이 만료됐습니다.";
    }

    if (jwtException instanceof MalformedJwtException) {
      logger.error("토큰 구조가 올바르지 않습니다.");
      return "유효하지 않은 토큰입니다.";
    }

    if (jwtException instanceof SignatureException) {
      logger.error("토큰의 서명이 올바르지 않습니다.");
      return "유효하지 않은 토큰입니다.";
    }

    if (jwtException instanceof UnsupportedJwtException) {
      logger.error("지원하지 않는 JWT 토큰입니다.");
      return "유효하지 않은 토큰입니다.";
    }

    logger.warn("정의 되지 않은 JWT 예외 type: {}", jwtException.getClass().getSimpleName());
    return "유효하지 않은 토큰입니다.";
  }
}
