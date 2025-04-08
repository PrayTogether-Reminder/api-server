package site.praytogether.pray_together.domain.prayer.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;

public interface PrayerTitleRepository extends JpaRepository<PrayerTitle, Long> {}
