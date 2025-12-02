package site.praytogether.pray_together.domain.auth.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByMemberId(Long memberId);

  void deleteByMemberId(Long memberId);

  boolean existsByMemberId(Long memberId);
}
