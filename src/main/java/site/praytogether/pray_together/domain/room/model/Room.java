package site.praytogether.pray_together.domain.room.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.praytogether.pray_together.constant.CoreConstant.RoomConstant;
import site.praytogether.pray_together.domain.base.BaseEntity;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "room")
@SequenceGenerator(
    name = "ROOM_SEQ_GENERATOR",
    sequenceName = "ROOM_SEQ",
    initialValue = 1,
    allocationSize = 50)
public class Room extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROOM_SEQ_GENERATOR")
  private Long id;

  @Column(nullable = false, length = RoomConstant.NAME_MAX_LEN)
  private String name;

  @Column(nullable = false, length = RoomConstant.DESCRIPTION_MAX_LEN)
  private String description;
}
