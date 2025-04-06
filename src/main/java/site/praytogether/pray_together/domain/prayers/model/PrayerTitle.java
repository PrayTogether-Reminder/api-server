package site.praytogether.pray_together.domain.prayers.model;

import static site.praytogether.pray_together.constant.CoreConstant.PrayerTitleConstant.TITLE_MAX_LEN;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import site.praytogether.pray_together.domain.base.BaseEntity;
import site.praytogether.pray_together.domain.room.model.Room;

@Entity
@Table(name = "prayer_title")
@Getter
@NoArgsConstructor
public class PrayerTitle extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prayer_title_seq_generator")
  @SequenceGenerator(
      name = "prayer_title_seq_generator",
      sequenceName = "prayer_title_seq",
      allocationSize = 50)
  private Long id;

  @JoinColumn(name = "room_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Room room;

  @Column(nullable = false, length = TITLE_MAX_LEN)
  private String title;

  @OneToMany(
      mappedBy = "prayerTitle",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<PrayerContent> prayerContents = new ArrayList<>();
}
