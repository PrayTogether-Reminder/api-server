package site.praytogether.pray_together.domain.room.dto;

import static site.praytogether.pray_together.constant.CoreConstant.MemberRoomConstant.ROOMS_INFINITE_SCROLL_SIZE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import site.praytogether.pray_together.domain.memberroom.model.RoomInfo;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomScrollResponse {
  private final List<RoomInfo> rooms;

  public static RoomScrollResponse of(LinkedHashMap<Long, RoomInfo> roomInfos) {
    RoomScrollResponse response =
        new RoomScrollResponse(new ArrayList<>(ROOMS_INFINITE_SCROLL_SIZE));
    roomInfos.forEach((key, value) -> response.add(value));
    return response;
  }

  private void add(RoomInfo info) {
    rooms.add(info);
  }
}
