package site.praytogether.pray_together.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.member.application.MemberApplicationService;
import site.praytogether.pray_together.domain.member.dto.MemberProfileResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
  MemberApplicationService memberApplication;

  @GetMapping("/me")
  public ResponseEntity<MemberProfileResponse> getProfile(@PrincipalId Long memberId) {
    MemberProfileResponse response = memberApplication.getProfile(memberId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
