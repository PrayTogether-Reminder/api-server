package site.praytogether.pray_together.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.notification.model.PrayerCompletionNotification;

public interface PrayerCompletionNotificationRepository
    extends JpaRepository<PrayerCompletionNotification, Long> {}
