package site.praytogether.pray_together.domain.prayer.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrayerTitleRepository extends JpaRepository<PrayerTitle, Long> {

  @Query(
      """
    SELECT pt
    FROM PrayerTitle pt
    LEFT JOIN FETCH pt.prayerContents
    WHERE pt.id = :titleId
""")
  Optional<PrayerTitle> findByIdWithContents(Long titleId);

  @Query(
      """
       SELECT pt
       FROM PrayerTitle pt
       JOIN FETCH pt.prayerCompletions pc
       WHERE pt.room.id = :roomId
       ORDER BY pt.createdTime DESC
""")
  List<PrayerTitle> findFirstPrayerTitleInfosOrderByCreatedTimeDesc(
      Long roomId, Pageable pageable);

  @Query(
      """
       SELECT pt
       FROM PrayerTitle pt
       JOIN FETCH pt.prayerCompletions pc
       WHERE pt.room.id = :roomId AND pt.createdTime < :after
       ORDER BY pt.createdTime DESC
""")
  List<PrayerTitle> findPrayerTitleInfosOrderByCreatedTimeDesc(
      Long roomId, Instant after, Pageable pageable);
}
