package site.praytogether.pray_together.domain.prayer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.base.BaseEntity;

@Entity
@Table(name = "PRAYER_COMPLETION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrayerCompletion extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prayer_completion_seq_generator")
  @SequenceGenerator(
      name = "prayer_completion_seq_generator",
      sequenceName = "prayer_completion_seq",
      allocationSize = 50)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "prayer_title_id")
  private PrayerTitle prayerTitle;

  @Column(name = "prayer_id", updatable = false, nullable = false)
  private Long prayerId;

  public static PrayerCompletion create(Long prayerId, PrayerTitle prayerTitle) {
    return PrayerCompletion.builder().prayerId(prayerId).prayerTitle(prayerTitle).build();
  }
}
