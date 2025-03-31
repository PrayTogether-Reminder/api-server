package site.praytogether.pray_together.domain.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.praytogether.pray_together.domain.room.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {}
