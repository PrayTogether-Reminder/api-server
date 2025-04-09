package site.praytogether.pray_together.domain.prayer.controller;

import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.DEFAULT_INFINITE_SCROLL_AFTER;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.prayer.application.PrayerApplicationService;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleScrollRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleScrollResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerUpdateRequest;

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

  @PutMapping("/{prayerTitleId}")
  public ResponseEntity<MessageResponse> updatePrayers(
      @PrincipalId Long memberId,
      @Positive(message = "잘 못된 기도 제목을 선택하셨습니다.") @PathVariable Long prayerTitleId,
      @Valid @RequestBody PrayerUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(prayerApplication.updatePrayers(memberId, prayerTitleId, request));
  }

  @GetMapping()
  public ResponseEntity<PrayerTitleScrollResponse> getPrayerTitlesByScroll(
      @NotNull(message = "잘 못된 방을 선택하셨습니다.") @Positive(message = "잘 못된 방을 선택하셨습니다.") @RequestParam
          Long roomId,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_AFTER) String after,
      @PrincipalId Long memberId) {
    PrayerTitleScrollRequest request = PrayerTitleScrollRequest.of(roomId, after);
    PrayerTitleScrollResponse response =
        prayerApplication.fetchPrayerTitleInfiniteScroll(memberId, request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{prayerTitleId}/contents")
  public ResponseEntity<PrayerContentResponse> getPrayerContent(
      @NotNull(message = "잘 못된 방을 선택하셨습니다.") @Positive(message = "잘 못된 방을 선택하셨습니다.") @PathVariable
          Long prayerTitleId,
      @PrincipalId Long memberId) {
    PrayerContentResponse response = prayerApplication.fetchPrayerContent(memberId, prayerTitleId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
