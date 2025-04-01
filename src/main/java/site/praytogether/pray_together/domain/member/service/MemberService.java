package site.praytogether.pray_together.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.member.expcetion.MemberAlreadyExistException;
import site.praytogether.pray_together.domain.member.expcetion.MemberNotFoundException;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  public Member getRefOrThrow(Long memberId) {
    validateMemberExists(memberId);
    return memberRepository.getReferenceById(memberId);
  }

  public void validateMemberExists(Long memberId) {
    if (isExistMember(memberId) == false) throw new MemberNotFoundException(memberId);
  }

  public boolean isExistMember(Long memberId) {
    return memberRepository.existsById(memberId);
  }

  public void validateMemberNotExists(String email) {
    if (isExistMember(email) == true) throw new MemberAlreadyExistException(email);
  }

  public boolean isExistMember(String email) {
    return memberRepository.existsByEmail(email);
  }
}
