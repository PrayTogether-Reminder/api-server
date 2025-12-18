package site.praytogether.pray_together.domain.notification.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import site.praytogether.pray_together.domain.notification.application.PrayerNotificationApplication;
import site.praytogether.pray_together.domain.prayer.domain.event.PrayerCompletionEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrayerCompletedEventListener {

  private final PrayerNotificationApplication prayerNotificationApplication;

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePrayerCompleted(PrayerCompletionEvent event) {
    try {
      prayerNotificationApplication.sendPrayerCompletion(event);
    } catch (Exception e) {
      log.error("[Notification] 기도 완료 알림 처리 실패: {}", e.getMessage(), e);
    }
  }
}
