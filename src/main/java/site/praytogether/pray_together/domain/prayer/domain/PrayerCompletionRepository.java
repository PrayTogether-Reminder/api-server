package site.praytogether.pray_together.domain.prayer.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.praytogether.pray_together.domain.prayer.infrastructure.persistence.PrayerCompletionWithMemberEntity;

public interface PrayerCompletionRepository extends JpaRepository<PrayerCompletion, Long> {

  /**
   * 주어진 PrayerTitle ID 목록에 해당하는 PrayerCompletion과 Member 정보를 한 번에 조회
   * LEFT JOIN을 사용하여 Member가 삭제된 경우에도 PrayerCompletion 정보는 조회됨
   *
   * @param titleIds PrayerTitle ID 목록
   * @return PrayerCompletion과 Member 정보를 담은 DTO 리스트
   */
  @Query(
      """
      SELECT new site.praytogether.pray_together.domain.prayer.infrastructure.persistence.PrayerCompletionWithMemberEntity(
        pc.id,
        pc.prayerTitle.id,
        pc.prayerId,
        m.name
      )
      FROM PrayerCompletion pc
      LEFT JOIN Member m ON pc.prayerId = m.id
      WHERE pc.prayerTitle.id IN :titleIds
  """)
  List<PrayerCompletionWithMemberEntity> findWithMemberByTitleIds(List<Long> titleIds);
}
