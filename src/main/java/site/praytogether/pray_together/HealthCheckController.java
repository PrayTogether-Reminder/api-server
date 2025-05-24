package site.praytogether.pray_together;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {
  @GetMapping
  public ResponseEntity<Map<String, String>> healthCheck() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "healthy");
    return ResponseEntity.ok(response);
  }
}
