package site.praytogether.pray_together.domain.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PrayerCompletionNotificationRepository
    extends JpaRepository<PrayerCompletionNotification, Long> {}
