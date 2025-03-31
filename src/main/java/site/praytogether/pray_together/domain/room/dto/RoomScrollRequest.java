package site.praytogether.pray_together.domain.room.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RoomScrollRequest {
  String orderBy;
  String after;
  String dir;

  public static RoomScrollRequest of(String orderBy, String after, String dir) {
    return new RoomScrollRequest(orderBy, after, dir);
  }
}
