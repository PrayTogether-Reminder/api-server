package site.praytogether.pray_together.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.notification.model.FcmToken;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
  boolean existsByTokenAndMember(String token, Member member);
}
