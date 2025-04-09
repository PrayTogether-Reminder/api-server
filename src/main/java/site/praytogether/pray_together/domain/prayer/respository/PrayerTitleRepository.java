package site.praytogether.pray_together.domain.prayer.respository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitleInfo;

public interface PrayerTitleRepository extends JpaRepository<PrayerTitle, Long> {

  @Query(
      """
    SELECT pt
    FROM PrayerTitle pt
    JOIN FETCH pt.prayerContents
    WHERE pt.id = :titleId
""")
  Optional<PrayerTitle> findByIdWithContents(Long titleId);

  @Query(
      """
       SELECT new site.praytogether.pray_together.domain.prayer.model.PrayerTitleInfo(
        pt.id, pt.title, pt.createdTime
       )
       FROM PrayerTitle pt
       WHERE pt.room.id = :roomId
       ORDER BY pt.createdTime DESC
""")
  List<PrayerTitleInfo> findFirstPrayerTitleInfosOrderByCreatedTimeDesc(
      Long roomId, Pageable pageable);

  @Query(
      """
       SELECT new site.praytogether.pray_together.domain.prayer.model.PrayerTitleInfo(
        pt.id, pt.title, pt.createdTime
       )
       FROM PrayerTitle pt
       WHERE pt.room.id = :roomId AND pt.createdTime < :after
       ORDER BY pt.createdTime DESC
""")
  List<PrayerTitleInfo> findPrayerTitleInfosOrderByCreatedTimeDesc(
      Long roomId, Instant after, Pageable pageable);
}
