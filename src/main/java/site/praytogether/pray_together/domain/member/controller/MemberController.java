package site.praytogether.pray_together.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.infrastructure.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.dto.SearchMemberResponse;
import site.praytogether.pray_together.domain.member.application.MemberApplicationService;
import site.praytogether.pray_together.domain.member.dto.MemberProfileResponse;
import site.praytogether.pray_together.domain.member.dto.UpdateProfileRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Slf4j
public class MemberController {
  private final MemberApplicationService memberApplication;

  @GetMapping("/me")
  public ResponseEntity<MemberProfileResponse> getProfile(@PrincipalId Long memberId) {
    log.info("[API] 내 정보 조회 시작 memberId={}", memberId);
    MemberProfileResponse response = memberApplication.getProfile(memberId);
    log.info("[API] 내 정보 조회 종료 memberId={}", memberId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/me")
  public ResponseEntity<MessageResponse> updateProfile(@PrincipalId Long memberId, @Valid @RequestBody UpdateProfileRequest request) {
    log.info("[API] 내 정보 수정 시작 memberId={}", memberId);
    MessageResponse response = memberApplication.updateProfile(memberId,request);
    log.info("[API] 내 정보 수정 종료 memberId={}", memberId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @GetMapping("/search")
  public ResponseEntity<SearchMemberResponse> searchMembers(@PrincipalId Long memberId, @RequestParam(name = "name")String searchName) {
    log.info("[API] 회원 검색 시작 memberId={}, name={}",memberId, searchName);
    SearchMemberResponse response = memberApplication.searchMembers(searchName);
    log.info("[API] 회원 검색 종료 memberId={}, name={}",memberId, searchName);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
