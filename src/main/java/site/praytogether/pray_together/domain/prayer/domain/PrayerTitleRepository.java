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

  /**
   * Room에 속한 PrayerTitle 목록을 최신순으로 조회 (첫 페이지)
   */
  @Query(
      """
       SELECT pt
       FROM PrayerTitle pt
       WHERE pt.room.id = :roomId
       ORDER BY pt.createdTime DESC
""")
  List<PrayerTitle> findByRoomIdOrderByCreatedTimeDesc(Long roomId, Pageable pageable);

  /**
   * Room에 속한 PrayerTitle 목록을 최신순으로 조회 (무한 스크롤용)
   *
   * @param after 이 시간 이전의 PrayerTitle만 조회 (커서 기반 페이징)
   */
  @Query(
      """
       SELECT pt
       FROM PrayerTitle pt
       WHERE pt.room.id = :roomId AND pt.createdTime < :after
       ORDER BY pt.createdTime DESC
""")
  List<PrayerTitle> findByRoomIdAndCreatedTimeBeforeOrderByCreatedTimeDesc(
      Long roomId, Instant after, Pageable pageable);
}
