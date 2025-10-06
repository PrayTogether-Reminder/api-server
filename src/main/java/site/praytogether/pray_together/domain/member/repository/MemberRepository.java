package site.praytogether.pray_together.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.praytogether.pray_together.domain.member.model.SearchResultMember;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.MemberProfile;

public interface MemberRepository extends JpaRepository<Member, Long> {
  boolean existsByEmail(String email);

  Optional<Member> findByEmail(String email);

  Optional<MemberProfile> findMemberProfileById(Long Id);

  @Query("""
    SELECT new site.praytogether.pray_together.domain.member.model.SearchResultMember(m.id,m.name,m.phoneNumber)
    
    FROM Member m
    WHERE m.name LIKE CONCAT('%',:name,'%')
    ORDER BY m.name ASC, m.phoneNumber.value ASC
""")
  List<SearchResultMember> findByNameContaining(@Param("name") String name);
}
