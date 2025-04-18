package site.praytogether.pray_together.domain.notification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.notification.model.PrayerCompletionNotification;
import site.praytogether.pray_together.domain.notification.repository.PrayerCompletionNotificationRepository;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;

@Service
@RequiredArgsConstructor
public class PrayerCompletionNotificationService {
  private final PrayerCompletionNotificationRepository notificationRepository;

  public void create(
      Long senderId, List<Long> recipientIds, String message, PrayerTitle prayerTitle) {
    List<PrayerCompletionNotification> notifications = new ArrayList<>();

    recipientIds.forEach(
        recipientId -> {
          if (Objects.equals(recipientId, senderId)) return;
          notifications.add(
              PrayerCompletionNotification.builder()
                  .prayerTitleId(prayerTitle.getId())
                  .recipientId(recipientId)
                  .senderId(senderId)
                  .message(message)
                  .build());
        });
    notificationRepository.saveAll(notifications);
  }
}
