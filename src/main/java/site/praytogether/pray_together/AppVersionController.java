package site.praytogether.pray_together;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/app-versions")
public class AppVersionController {


  private final String minAppVer;
  private final String updateForceAppVer;
  private final boolean maintenanceMode;

  public AppVersionController(@Value("${app.version.minimum}") String minAppVer,
      @Value("${app.version.update.force}") String updateForceAppVer,
      @Value("${app.maintenance.mode}") boolean maintenanceMode) {
    this.minAppVer = minAppVer;
    this.updateForceAppVer = updateForceAppVer;
    this.maintenanceMode = maintenanceMode;
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> appVersions() {
    Map<String, Object> versions = new HashMap<>();
    versions.put("minimumAppVersion", minAppVer);
    versions.put("forceUpdateAppVersion", updateForceAppVer);
    versions.put("maintenanceMode", maintenanceMode);
    return ResponseEntity.ok(versions);
  }
}
