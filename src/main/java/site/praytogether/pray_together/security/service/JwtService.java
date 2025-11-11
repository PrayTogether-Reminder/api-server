package site.praytogether.pray_together.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.constant.CoreConstant.JwtConstant;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;

@Service
public class JwtService {
  private final String TYPE = "type";
  private final String EMAIL = "email";
  private final String UID = "uid";

  private final Long accessTokenExpireTime; // ms
  private final Long refreshTokenExpireTime; // ms
  private final SecretKey secretKey;

  private JwtService(
      @Value("${jwt.secret-key}") String jwtSecretKey,
      @Value("${jwt.access-expire-time}") Long accessTokenExpireTime,
      @Value("${jwt.refresh-expire-time}") Long refreshTokenExpireTime) {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenExpireTime = accessTokenExpireTime;
    this.refreshTokenExpireTime = refreshTokenExpireTime;
  }

  public String issueAccessToken(PrayTogetherPrincipal principal) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(String.valueOf(principal.getId()))
        .claim(EMAIL, principal.getEmail())
        .claim(TYPE, JwtConstant.ACCESS_TYPE)
        .claim(UID, UUID.randomUUID().toString().substring(0, 6))
        .issuedAt(new Date(now))
        .expiration(new Date(now + accessTokenExpireTime))
        .signWith(secretKey)
        .compact();
  }

  public String issueRefreshToken(PrayTogetherPrincipal principal) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(String.valueOf(principal.getId()))
        .claim(EMAIL, principal.getEmail())
        .claim(TYPE, JwtConstant.REFRESH_TYPE)
        .claim(UID, UUID.randomUUID().toString().substring(0, 6))
        .issuedAt(new Date(now))
        .expiration(new Date(now + refreshTokenExpireTime))
        .signWith(secretKey)
        .compact();
  }

  public void isValid(String token) throws JwtException {
    Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
  }

  public String extractEmail(String token) {
    return extractAllClaims(token).get(EMAIL, String.class);
  }

  public String extractType(String token) {
    return extractAllClaims(token).get(TYPE, String.class);
  }

  public Long extractMemberId(String token) {
    return Long.valueOf(extractAllClaims(token).getSubject());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public PrayTogetherPrincipal extractPrincipal(String token) {
    Claims claims = extractAllClaims(token);
    return PrayTogetherPrincipal.builder()
        .email((String) claims.get(EMAIL))
        .id(Long.valueOf(claims.getSubject()))
        .build();
  }
}
