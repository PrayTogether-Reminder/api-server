package site.praytogether.pray_together.domain.auth.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import site.praytogether.pray_together.domain.auth.domain.exception.AppleTokenVerificationException;

@Component
@Slf4j
public class AppleTokenVerifier {

  private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
  private static final String APPLE_ISSUER = "https://appleid.apple.com";
  private static final long CACHE_TTL_SECONDS = 86400; // 24시간

  private final String clientId;
  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate;
  private final Map<String, CachedPublicKey> publicKeyCache;

  public AppleTokenVerifier(@Value("${apple.oauth.client-id}") String clientId) {
    this.clientId = clientId;
    this.objectMapper = new ObjectMapper();
    this.restTemplate = new RestTemplate();
    this.publicKeyCache = new ConcurrentHashMap<>();
  }

  public String verify(String idToken) {
    try {
      // 1. 헤더에서 kid, alg 추출
      JwtHeader header = extractHeader(idToken);

      // 2. alg 검증 (RS256만 허용)
      if (!"RS256".equals(header.alg)) {
        throw new AppleTokenVerificationException("Invalid algorithm: " + header.alg);
      }

      // 3. 공개키 가져오기 (캐시 또는 Apple 서버)
      PublicKey publicKey = getPublicKey(header.kid);

      // 4. 서명 검증 + 클레임 검증 (iss, aud, exp)
      Claims claims = Jwts.parser()
          .verifyWith(publicKey)
          .requireIssuer(APPLE_ISSUER)
          .requireAudience(clientId)
          .build()
          .parseSignedClaims(idToken)
          .getPayload();

      // 5. sub (Apple User ID) 반환
      return claims.getSubject();
    } catch (AppleTokenVerificationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Apple ID Token 검증 실패: {}", e.getMessage());
      throw new AppleTokenVerificationException(e.getMessage());
    }
  }

  private JwtHeader extractHeader(String idToken) {
    try {
      String[] parts = idToken.split("\\.");
      if (parts.length != 3) {
        throw new AppleTokenVerificationException("Invalid JWT format");
      }

      String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
      JsonNode header = objectMapper.readTree(headerJson);

      return new JwtHeader(
          header.get("kid").asText(),
          header.get("alg").asText()
      );
    } catch (AppleTokenVerificationException e) {
      throw e;
    } catch (Exception e) {
      throw new AppleTokenVerificationException("Failed to extract header from token: " + e.getMessage());
    }
  }

  private PublicKey getPublicKey(String kid) {
    // 1. 캐시 확인 (TTL 체크)
    CachedPublicKey cached = publicKeyCache.get(kid);
    if (cached != null && !cached.isExpired()) {
      return cached.publicKey;
    }

    // 2. 캐시 미스 또는 만료 -> Apple 서버에서 가져오기
    PublicKey publicKey = fetchPublicKeyFromApple(kid);

    // 3. 캐시에 저장
    publicKeyCache.put(kid, new CachedPublicKey(publicKey, Instant.now().plusSeconds(CACHE_TTL_SECONDS)));

    return publicKey;
  }

  private PublicKey fetchPublicKeyFromApple(String kid) {
    try {
      String response = restTemplate.getForObject(APPLE_PUBLIC_KEYS_URL, String.class);
      JsonNode keys = objectMapper.readTree(response).get("keys");

      for (JsonNode keyNode : keys) {
        if (kid.equals(keyNode.get("kid").asText())) {
          return createPublicKey(keyNode);
        }
      }

      // 키를 찾지 못한 경우 캐시 전체 무효화 후 재시도
      publicKeyCache.clear();
      log.warn("Public key not found for kid: {}. Cache cleared.", kid);

      throw new AppleTokenVerificationException("Public key not found for kid: " + kid);
    } catch (AppleTokenVerificationException e) {
      throw e;
    } catch (Exception e) {
      throw new AppleTokenVerificationException("Failed to fetch Apple public keys: " + e.getMessage());
    }
  }

  private PublicKey createPublicKey(JsonNode keyNode) throws Exception {
    String n = keyNode.get("n").asText();
    String e = keyNode.get("e").asText();

    byte[] nBytes = Base64.getUrlDecoder().decode(n);
    byte[] eBytes = Base64.getUrlDecoder().decode(e);

    BigInteger modulus = new BigInteger(1, nBytes);
    BigInteger exponent = new BigInteger(1, eBytes);

    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(spec);
  }

  private record JwtHeader(String kid, String alg) {}

  private record CachedPublicKey(PublicKey publicKey, Instant expiresAt) {
    boolean isExpired() {
      return Instant.now().isAfter(expiresAt);
    }
  }
}
