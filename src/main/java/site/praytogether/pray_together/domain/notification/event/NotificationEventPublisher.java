package site.praytogether.pray_together.domain.notification.event;

import org.springframework.stereotype.Component;

@Component
public class NotificationEventPublisher implements EventPublisher {

  @Override
  public void publishPrayerComplete() {}
}
