package site.praytogether.pray_together.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class PrayTogetherUserDetailsService implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member =
        memberRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다."));
    return PrayTogetherPrincipal.builder()
        .id(member.getId())
        .email(member.getEmail())
        .password(member.getPassword())
        .build();
  }
}
