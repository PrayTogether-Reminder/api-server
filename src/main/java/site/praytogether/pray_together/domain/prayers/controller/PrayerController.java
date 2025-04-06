package site.praytogether.pray_together.domain.prayers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.prayers.application.PrayerApplicationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prayers")
public class PrayerController {
  private final PrayerApplicationService prayerApplication;
}
