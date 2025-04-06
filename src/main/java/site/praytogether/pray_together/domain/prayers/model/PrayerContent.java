package site.praytogether.pray_together.domain.prayers.model;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.base.BaseEntity;

@Entity
@Table(name = "prayer_content")
@Getter
@NoArgsConstructor
public class PrayerContent extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prayer_content_seq_generator")
  @SequenceGenerator(
      name = "prayer_content_seq_generator",
      sequenceName = "prayer_content_seq",
      allocationSize = 50)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "prayer_title_id", nullable = false)
  private PrayerTitle prayerTitle;

  @Column(name = "member_id")
  private Long memberId;

  @Column(nullable = false, length = 30)
  private String memberName;

  @Column(nullable = false, columnDefinition = "text")
  private String content;
}
