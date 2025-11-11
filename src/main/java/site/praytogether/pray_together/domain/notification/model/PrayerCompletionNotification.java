package site.praytogether.pray_together.domain.notification.model;

import static site.praytogether.pray_together.domain.notification.constant.NotificationType.PRAYER_COMPLETION;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "PRAYER_COMPLETION_NOTIFICATION")
@DiscriminatorValue(PRAYER_COMPLETION)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerCompletionNotification extends Notification {

  @Column(name = "prayer_title_id", nullable = false)
  private Long prayerTitleId;

  @Builder
  private PrayerCompletionNotification(
      Long senderId, Long recipientId, String message, Long prayerTitleId) {
    super(null, senderId, recipientId, message);
    this.prayerTitleId = prayerTitleId;
  }
}
