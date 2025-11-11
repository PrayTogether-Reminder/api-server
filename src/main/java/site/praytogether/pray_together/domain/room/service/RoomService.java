package site.praytogether.pray_together.domain.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.room.exception.RoomNotFoundException;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.repository.RoomRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

  private final RoomRepository roomRepository;

  @Transactional
  public Room createRoom(String name, String description) {
    Room newRoom = Room.create(name, description);
    return roomRepository.save(newRoom);
  }

  public void validateRoomExist(Long roomId) {
    if (roomRepository.existsById(roomId) == false) throw new RoomNotFoundException(roomId);
  }

  public Room getRefOrThrow(Long roomId) {
    validateRoomExist(roomId);
    return roomRepository.getReferenceById(roomId);
  }
}
