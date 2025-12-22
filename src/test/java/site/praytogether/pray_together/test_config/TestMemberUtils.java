package site.praytogether.pray_together.test_config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.PhoneNumber;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;

/**
 * 회원 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestMemberUtils {

  private final MemberRepository memberRepository;
  private static int emailUniqueId = 0;

  public Member createSave() {
    Member member = Member.create(
        "test" + (emailUniqueId),
        "test@test.com" + (emailUniqueId++),
        "test",
        PhoneNumber.of("010-1234-5678"));
    memberRepository.save(member);
    return member;
  }

  public Member createSave_With_Name_And_PhoneNumber(String name, String phoneNumber) {
    Member member = Member.create(
        name,
        "test@test.com" + (emailUniqueId++),
        "test",
        PhoneNumber.of(phoneNumber));
    memberRepository.save(member);
    return member;
  }
}
