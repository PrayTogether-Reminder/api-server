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
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;

@Entity
@Table(name = "prayer_content")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

  @Column(name = "writer_id",nullable = false)
  private Long writerId;

  @Column(name = "writer_name",nullable = false, length = 30)
  private String writerName;

  @Column(name = "member_id")
  private Long memberId;

  @Column(nullable = false, length = 30)
  private String memberName;

  @Column(nullable = false, columnDefinition = "CLOB")
  private String content;

  public static PrayerContent create(PrayerTitle title, PrayerContentCreateRequest reqContent, Member writer) {
    return PrayerContent.builder()
        .prayerTitle(title)
        .writerId(writer.getId())
        .writerName(writer.getName())
        .memberId(reqContent.getMemberId())
        .memberName(reqContent.getMemberName())
        .content(reqContent.getContent())
        .build();
  }

  public void updateContentAndWriter(String content, Member writer) {
    this.content = content;
    this.writerId = writer.getId();
    this.writerName = writer.getName();
  }

  public void detachFromTitle() {
    this.prayerTitle = null;
  }
}
