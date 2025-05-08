package site.praytogether.pray_together.domain.notification.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.notification.model.FcmToken;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
  boolean existsByTokenAndMember(String token, Member member);

  @Query(
      """
        SELECT ft
        FROM FcmToken ft
        WHERE ft.member.id IN :memberIds

      """)
  List<FcmToken> findByMemberIds(List<Long> memberIds);
}
