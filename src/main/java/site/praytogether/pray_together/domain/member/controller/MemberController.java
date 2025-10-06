package site.praytogether.pray_together.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.praytogether.pray_together.domain.auth.annotation.PrincipalId;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.dto.SearchMemberResponse;
import site.praytogether.pray_together.domain.member.application.MemberApplicationService;
import site.praytogether.pray_together.domain.member.dto.MemberProfileResponse;
import site.praytogether.pray_together.domain.member.dto.UpdateProfileRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
  private final MemberApplicationService memberApplication;

  @GetMapping("/me")
  public ResponseEntity<MemberProfileResponse> getProfile(@PrincipalId Long memberId) {
    MemberProfileResponse response = memberApplication.getProfile(memberId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/me")
  public ResponseEntity<MessageResponse> updateProfile(@PrincipalId Long memberId, @Valid @RequestBody UpdateProfileRequest request) {
    MessageResponse response = memberApplication.updateProfile(memberId,request);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @GetMapping("/search")
  public ResponseEntity<SearchMemberResponse> searchMembers(@RequestParam(name = "name")String searchName) {
    SearchMemberResponse response = memberApplication.searchMembers(searchName);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
