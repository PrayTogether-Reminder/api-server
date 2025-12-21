package site.praytogether.pray_together.test_config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.repository.RoomRepository;

/**
 * 방 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestRoomUtils {

  private final RoomRepository roomRepository;
  private static int roomUniqueId = 0;

  public Room createSave() {
    Room room = Room.create(
        "test-Room" + roomUniqueId++,
        "test-description" + roomUniqueId++);
    roomRepository.save(room);
    return room;
  }
}
