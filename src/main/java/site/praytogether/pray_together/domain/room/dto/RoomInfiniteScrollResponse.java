package site.praytogether.pray_together.domain.room.dto;

import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.ROOMS_INFINITE_SCROLL_SIZE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import site.praytogether.pray_together.domain.member_room.model.RoomInfo;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomInfiniteScrollResponse {
  private final List<RoomInfo> rooms;

  public static RoomInfiniteScrollResponse from(LinkedHashMap<Long, RoomInfo> roomInfos) {
    RoomInfiniteScrollResponse response =
        new RoomInfiniteScrollResponse(new ArrayList<>(ROOMS_INFINITE_SCROLL_SIZE));
    roomInfos.forEach((key, value) -> response.add(value));
    return response;
  }

  private void add(RoomInfo info) {
    rooms.add(info);
  }
}
