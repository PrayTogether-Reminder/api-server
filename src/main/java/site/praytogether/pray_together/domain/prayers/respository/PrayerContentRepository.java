package site.praytogether.pray_together.domain.prayers.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.prayers.model.PrayerContent;

public interface PrayerContentRepository extends JpaRepository<PrayerContent, Long> {}
