package site.praytogether.pray_together.domain.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.auth.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByMemberId(Long memberId);

  void deleteByMemberId(Long memberId);

  boolean existsByMemberId(Long memberId);
}
