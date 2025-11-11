package site.praytogether.pray_together.domain.room.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomInfiniteScrollRequest {
  private String orderBy;
  private String after;
  private String dir;

  public static RoomInfiniteScrollRequest of(String orderBy, String after, String dir) {
    return new RoomInfiniteScrollRequest(orderBy, after, dir);
  }
}
