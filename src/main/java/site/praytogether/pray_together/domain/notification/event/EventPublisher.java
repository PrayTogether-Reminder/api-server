package site.praytogether.pray_together.domain.notification.event;

import org.springframework.stereotype.Component;

@Component
public interface EventPublisher {
  public void publishPrayerComplete();
}
