package site.praytogether.pray_together.domain.prayer.controller;

import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.DEFAULT_INFINITE_SCROLL_AFTER;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.infrastructure.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.prayer.application.PrayerApplicationService;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCompletionCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentUpdateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleInfiniteScrollRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleInfiniteScrollResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleResponse;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleUpdateRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prayers")
@Slf4j
public class PrayerController {
  private final PrayerApplicationService prayerApplication;

  // ========== 기도 제목 관련 API ==========
  
  @PostMapping
  public ResponseEntity<PrayerTitleResponse> createPrayerTitle(
      @PrincipalId Long memberId,
      @Valid @RequestBody PrayerTitleCreateRequest request) {
    log.info("[API] 기도 제목 생성 시작 memberId={}", memberId);
    PrayerTitleResponse response = prayerApplication.createPrayerTitle(memberId, request);
    log.info("[API] 기도 제목 생성 종료 memberId={} titleId={}", memberId, response.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{titleId}")
  public ResponseEntity<MessageResponse> updatePrayerTitle(
      @PrincipalId Long memberId,
      @Positive(message = "잘 못된 기도 제목을 선택하셨습니다.") @PathVariable Long titleId,
      @Valid @RequestBody PrayerTitleUpdateRequest request) {
    log.info("[API] 기도 제목 수정 시작 memberId={} titleId={}", memberId, titleId);
    MessageResponse response = prayerApplication.updatePrayerTitle(memberId, titleId, request);
    log.info("[API] 기도 제목 수정 종료 memberId={} titleId={}", memberId, titleId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{titleId}")
  public ResponseEntity<MessageResponse> deletePrayerTitle(
      @PrincipalId Long memberId,
      @Positive(message = "잘 못된 기도 제목을 선택하셨습니다.") @PathVariable Long titleId) {
    log.info("[API] 기도 제목 삭제 시작 memberId={} titleId={}", memberId, titleId);
    MessageResponse response = prayerApplication.deletePrayerTitle(memberId, titleId);
    log.info("[API] 기도 제목 삭제 종료 memberId={} titleId={}", memberId, titleId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping
  public ResponseEntity<PrayerTitleInfiniteScrollResponse> getPrayerTitlesByInfiniteScroll(
      @NotNull(message = "잘 못된 방을 선택하셨습니다.") 
      @Positive(message = "잘 못된 방을 선택하셨습니다.") 
      @RequestParam Long roomId,
      @RequestParam(defaultValue = DEFAULT_INFINITE_SCROLL_AFTER) String after,
      @PrincipalId Long memberId) {
    log.info("[API] 기도 제목 무한스크롤 조회 시작 memberId={} roomId={} after={}", memberId, roomId, after);
    PrayerTitleInfiniteScrollRequest request = PrayerTitleInfiniteScrollRequest.of(roomId, after);
    PrayerTitleInfiniteScrollResponse response =
        prayerApplication.fetchPrayerTitleInfiniteScroll(memberId, request);
    log.info("[API] 기도 제목 무한스크롤 조회 종료 memberId={} roomId={}", memberId, roomId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ========== 기도 내용 관련 API ==========
  
  @PostMapping("/{titleId}/contents")
  public ResponseEntity<MessageResponse> createPrayerContent(
      @PrincipalId Long writerId,
      @NotNull(message = "잘 못된 기도 제목을 선택하셨습니다.") 
      @Positive(message = "잘 못된 기도 제목을 선택하셨습니다.") 
      @PathVariable Long titleId,
      @Valid @RequestBody PrayerContentCreateRequest request) {
    log.info("[API] 기도 내용 생성 시작 writerId={} titleId={}", writerId, titleId);
    MessageResponse response = prayerApplication.createPrayerContent(writerId, titleId, request);
    log.info("[API] 기도 내용 생성 종료 writerId={} titleId={}", writerId, titleId);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{titleId}/contents/{contentId}")
  public ResponseEntity<MessageResponse> updatePrayerContent(
      @PrincipalId Long memberId,
      @NotNull(message = "잘 못된 기도 제목을 선택하셨습니다.") 
      @Positive(message = "잘 못된 기도 제목을 선택하셨습니다.") 
      @PathVariable Long titleId,
      @NotNull(message = "잘 못된 기도 내용을 선택하셨습니다.") 
      @Positive(message = "잘 못된 기도 내용을 선택하셨습니다.") 
      @PathVariable Long contentId,
      @Valid @RequestBody PrayerContentUpdateRequest request) {
    log.info(
        "[API] 기도 내용 수정 시작 memberId={} titleId={} contentId={}",
        memberId, titleId, contentId);
    MessageResponse response =
        prayerApplication.updatePrayerContent(memberId, titleId, contentId, request);
    log.info(
        "[API] 기도 내용 수정 종료 memberId={} titleId={} contentId={}",
        memberId, titleId, contentId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{titleId}/contents")
  public ResponseEntity<PrayerContentResponse> getPrayerContents(
      @PrincipalId Long memberId,
      @NotNull(message = "잘 못된 기도 제목을 선택하셨습니다.") 
      @Positive(message = "잘 못된 기도 제목을 선택하셨습니다.") 
      @PathVariable Long titleId) {
    log.info("[API] 기도 내용 조회 시작 memberId={} titleId={}", memberId, titleId);
    PrayerContentResponse response = prayerApplication.fetchPrayerContents(memberId, titleId);
    log.info("[API] 기도 내용 조회 종료 memberId={} titleId={}", memberId, titleId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{titleId}/contents/{contentId}")
  public ResponseEntity<MessageResponse> deletePrayerContent(
      @PrincipalId Long memberId,
      @NotNull(message = "잘 못된 기도 제목을 선택하셨습니다.") 
      @Positive(message = "잘 못된 기도 제목을 선택하셨습니다.") 
      @PathVariable Long titleId,
      @NotNull(message = "잘 못된 기도 내용을 선택하셨습니다.") 
      @Positive(message = "잘 못된 기도 내용을 선택하셨습니다.") 
      @PathVariable Long contentId) {
    log.info(
        "[API] 기도 내용 삭제 시작 memberId={} titleId={} contentId={}",
        memberId, titleId, contentId);
    MessageResponse response =
        prayerApplication.deletePrayerContent(memberId, titleId, contentId);
    log.info(
        "[API] 기도 내용 삭제 종료 memberId={} titleId={} contentId={}",
        memberId, titleId, contentId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // ========== 기타 기능 API ==========
  
  @PostMapping("/{titleId}/completion")
  public ResponseEntity<MessageResponse> completePrayer(
      @PrincipalId Long memberId,
      @NotNull(message = "잘 못된 기도제목 입니다.") @Positive(message = "잘 못된 기도제목 입니다.") @PathVariable
          Long titleId,
      @Valid @RequestBody PrayerCompletionCreateRequest request) {
    log.info("[API] 기도 완료 처리 시작 memberId={} titleId={}", memberId, titleId);
    MessageResponse response =
        prayerApplication.completePrayerAndNotify(memberId, titleId, request);
    log.info("[API] 기도 완료 처리 종료 memberId={} titleId={}", memberId, titleId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
