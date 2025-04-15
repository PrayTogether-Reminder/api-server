package site.praytogether.pray_together.domain.notification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.base.BaseEntity;

@Entity
@Getter
@Table(name = "notification")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
    name = "notification_type",
    discriminatorType = DiscriminatorType.STRING,
    length = 50)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_generator")
  @SequenceGenerator(
      name = "notification_seq_generator",
      sequenceName = "notification_seq",
      allocationSize = 50)
  private Long id;

  @Column(name = "sender_id", nullable = false)
  private Long senderId;

  @Column(name = "recipient_id", nullable = false)
  private Long recipientId;

  @Column(name = "message", nullable = false, length = 500)
  private String message;
}
