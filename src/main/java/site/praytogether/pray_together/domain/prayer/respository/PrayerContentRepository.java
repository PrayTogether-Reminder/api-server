package site.praytogether.pray_together.domain.prayer.respository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;

public interface PrayerContentRepository extends JpaRepository<PrayerContent, Long> {

  @Query(
      """
    SELECT new site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo(
       pc.id,pc.writerId,pc.writerName,pc.memberId,pc.memberName,pc.content
        )
    FROM PrayerContent pc
    WHERE pc.prayerTitle.id = :titleId
    ORDER BY pc.memberName ASC
""")
  List<PrayerContentInfo> findPrayerContentsByTitleId(Long titleId);

  boolean existsByIdAndPrayerTitleId(Long contentId, Long titleId);
  
  boolean existsByPrayerTitleIdAndMemberName(Long titleId, String memberName);
}
