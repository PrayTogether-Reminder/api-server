package site.praytogether.pray_together.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.member.dto.MemberProfileResponse;
import site.praytogether.pray_together.domain.member.model.MemberProfile;
import site.praytogether.pray_together.domain.member.service.MemberService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberApplicationService {
  private final MemberService memberService;

  public MemberProfileResponse getProfile(Long memberId) {
    MemberProfile profile = memberService.fetchProfileById(memberId);
    return MemberProfileResponse.from(profile);
  }
}
