package site.praytogether.pray_together.domain.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.member.expcetion.MemberAlreadyExistException;
import site.praytogether.pray_together.domain.member.expcetion.MemberNotFoundException;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.MemberProfile;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void createMember(String name, String email, String password) {
    Member createdMember = Member.create(name, email, passwordEncoder.encode(password));
    memberRepository.save(createdMember);
  }

  @Transactional
  public void deleteMember(Long memberId) {
    memberRepository.deleteById(memberId);
  }

  public Member getRefOrThrow(Long memberId) {
    validateMemberExists(memberId);
    return memberRepository.getReferenceById(memberId);
  }

  public Member getByEmail(String email) {
    return memberRepository
        .findByEmail(email)
        .orElseThrow(() -> new MemberNotFoundException(email));
  }

  public Member fetchById(Long id) {
    return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
  }

  public List<Member> fetchByIds(List<Long> ids) {
    return memberRepository.findAllById(ids);
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

  public MemberProfile fetchProfileById(Long memberId) {
    return memberRepository
        .findMemberProfileById(memberId)
        .orElseThrow(() -> new MemberNotFoundException(memberId));
  }
}
