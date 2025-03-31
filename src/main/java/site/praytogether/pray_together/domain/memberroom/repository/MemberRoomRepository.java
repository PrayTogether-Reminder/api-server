package site.praytogether.pray_together.domain.memberroom.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.praytogether.pray_together.domain.memberroom.model.MemberRoom;
import site.praytogether.pray_together.domain.memberroom.model.RoomIdMemberCnt;
import site.praytogether.pray_together.domain.memberroom.model.RoomInfo;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

  @Query(
      """
      SELECT new site.praytogether.pray_together.domain.memberroom.model.RoomInfo(
        r.id ,r.name, r.description,mr.createdTime ,mr.isNotification
      )
      FROM MemberRoom  mr
      JOIN Room  r ON mr.room.id = r.id
      WHERE mr.member.id = :memberId
      ORDER BY mr.createdTime DESC
""")
  List<RoomInfo> findFirstRoomInfoOrderByJoinedTimeDesc(Long memberId, Pageable pageable);

  @Query(
      """
          SELECT new site.praytogether.pray_together.domain.memberroom.model.RoomInfo(
            r.id ,r.name, r.description,mr.createdTime ,mr.isNotification
          )
          FROM MemberRoom  mr
          JOIN Room  r ON mr.room.id = r.id
          WHERE mr.member.id = :memberId AND mr.createdTime < :joinedTime
          ORDER BY mr.createdTime DESC
    """)
  List<RoomInfo> findRoomInfoOrderByJoinedTimeDesc(
      Long memberId, Instant joinedTime, Pageable pageable);

  @Query(
      """
        SELECT new site.praytogether.pray_together.domain.memberroom.model.RoomIdMember(
        mr.room.id, COUNT(*)
        )
        FROM MemberRoom mr
        WHERE mr.id IN :roomIds
        GROUP BY mr.room.id

""")
  List<RoomIdMemberCnt> findMemberCntByIds(List<Long> roomIds);
}
