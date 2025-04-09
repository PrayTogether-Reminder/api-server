package site.praytogether.pray_together.domain.room.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomScrollRequest {
  private String orderBy;
  private String after;
  private String dir;

  public static RoomScrollRequest of(String orderBy, String after, String dir) {
    return new RoomScrollRequest(orderBy, after, dir);
  }
}
