package site.praytogether.pray_together.domain.auth.infrastructure;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.auth.domain.exception.GoogleTokenVerificationException;

@Component
@Slf4j
public class GoogleTokenVerifier {

  private final GoogleIdTokenVerifier verifier;

  public GoogleTokenVerifier(@Value("${google.oauth.web-client-id}") String webClientId) {
    this.verifier = new GoogleIdTokenVerifier.Builder(
        new NetHttpTransport(), GsonFactory.getDefaultInstance())
        .setAudience(Collections.singletonList(webClientId))
        .build();
  }

  public GoogleIdToken.Payload verify(String idToken) {
    try {
      GoogleIdToken googleIdToken = verifier.verify(idToken);
      if (googleIdToken == null) {
        throw new GoogleTokenVerificationException("Invalid Google ID Token");
      }
      return googleIdToken.getPayload();
    } catch (Exception e) {
      log.error("Google ID Token 검증 실패: {}", e.getMessage());
      throw new GoogleTokenVerificationException(e.getMessage());
    }
  }
}
