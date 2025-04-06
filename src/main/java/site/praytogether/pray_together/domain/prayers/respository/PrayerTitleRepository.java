package site.praytogether.pray_together.domain.prayers.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.prayers.model.PrayerTitle;

public interface PrayerTitleRepository extends JpaRepository<PrayerTitle, Long> {}
