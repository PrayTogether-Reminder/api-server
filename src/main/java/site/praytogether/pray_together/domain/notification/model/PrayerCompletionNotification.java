package site.praytogether.pray_together.domain.notification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "prayer_completion_notification")
@DiscriminatorValue("PRAYER_COMPLETION")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerCompletionNotification extends Notification {

  @Column(name = "prayer_title_id", nullable = false)
  private Long prayerTitleId;
}
