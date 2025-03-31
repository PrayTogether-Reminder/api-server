package site.praytogether.pray_together.domain.member.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  public Optional<Member> getRefIfExist(Long memberId) {
    if (isExistMember(memberId)) {
      return Optional.of(memberRepository.getReferenceById(memberId));
    }
    return Optional.empty();
  }

  public boolean isExistMember(Long memberId) {
    return memberRepository.existsById(memberId);
  }
}
