package site.praytogether.pray_together.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.dto.MemberProfileResponse;
import site.praytogether.pray_together.domain.member.dto.UpdateProfileRequest;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.MemberProfile;
import site.praytogether.pray_together.domain.member.model.UpdateProfileCommand;
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

  @Transactional
  public MessageResponse updateProfile(Long memberId, UpdateProfileRequest request) {
    UpdateProfileCommand command = UpdateProfileCommand.from(request);
    Member member = memberService.fetchById(memberId);

    if(command.getName() != null) {
      member.updateName(command.getName());
    }

    if(command.getPhoneNumber() != null) {
      member.updatePhoneNumber(command.getPhoneNumber());
    }

    return MessageResponse.of("프로필을 변경했습니다.");
  }
}
