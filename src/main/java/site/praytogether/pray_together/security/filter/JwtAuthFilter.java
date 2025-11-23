package site.praytogether.pray_together.security.filter;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static site.praytogether.pray_together.domain.auth.exception.AuthExceptionSpec.INCORRECT_EMAIL_PASSWORD;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.praytogether.pray_together.domain.auth.dto.LoginRequest;
import site.praytogether.pray_together.domain.auth.dto.LoginResponse;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.auth.service.RefreshTokenService;
import site.praytogether.pray_together.exception.ExceptionResponse;
import site.praytogether.pray_together.security.service.JwtService;

public class JwtAuthFilter extends UsernamePasswordAuthenticationFilter {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final AuthenticationManager authenticationManager;
  private final ObjectMapper objectMapper;
  private final RefreshTokenService refreshTokenService;
  private final String LOGIN_URL = "/api/v1/auth/login";
  private final JwtService jwtService;

  public JwtAuthFilter(
      AuthenticationManager authenticationManager,
      ObjectMapper objectMapper,
      RefreshTokenService refreshTokenService,
      JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.objectMapper = objectMapper;
    this.refreshTokenService = refreshTokenService;
    this.jwtService = jwtService;
    setFilterProcessesUrl(LOGIN_URL);
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
      LoginRequest dto = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
      logger.info("[API] 로그인 요청 시도 {}", dto.getEmail());
      return authenticationManager.authenticate(createAuthToken(dto));
    } catch (IOException e) {
      logger.error("[API] 로그인 요청 parse 오류 : ", e);
      throw new RuntimeException("[API] 로그인 요청 parse 오류 : ", e);
    }
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    logger.error("[API] 로그인 실패 : {}", failed.getMessage());
    response.setStatus(SC_UNAUTHORIZED);
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(UTF_8.name());
    response
        .getWriter()
        .write(
            objectMapper.writeValueAsString(
                ExceptionResponse.of(
                    INCORRECT_EMAIL_PASSWORD.getStatus().value(),
                    INCORRECT_EMAIL_PASSWORD.getCode(),
                    "이메일 또는 비밀번호가 일치하지 않습니다.")));
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication)
      throws IOException {
    PrayTogetherPrincipal principal = (PrayTogetherPrincipal) authentication.getPrincipal();
    String accessToken = jwtService.issueAccessToken(principal);
    String refreshToken = jwtService.issueRefreshToken(principal);
    refreshTokenService.save(
        principal.getId(), refreshToken, jwtService.extractExpiration(refreshToken));

    LoginResponse loginResponse =
        LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(UTF_8.name());
    response.setStatus(SC_OK);
    logger.info("[API] 로그인 요청 종료 {}", principal.getEmail());
  }

  private UsernamePasswordAuthenticationToken createAuthToken(LoginRequest request) {
    return new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
  }
}
