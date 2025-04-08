package site.praytogether.pray_together.domain.prayer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.prayer.application.PrayerApplicationService;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCreateRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prayers")
public class PrayerController {
  private final PrayerApplicationService prayerApplication;

  @PostMapping
  public ResponseEntity<MessageResponse> createPrayers(
      @PrincipalId Long memberId, @Valid @RequestBody PrayerCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(prayerApplication.createPrayers(memberId, request));
  }
}
