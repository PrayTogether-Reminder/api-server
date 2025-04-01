package site.praytogether.pray_together.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByEmail(String email);
}
