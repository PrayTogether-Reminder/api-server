package site.praytogether.pray_together.domain.member_room.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.praytogether.pray_together.domain.member.model.MemberIdName;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.member_room.model.RoomIdMemberCount;
import site.praytogether.pray_together.domain.member_room.model.RoomInfo;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

  int deleteByMember_IdAndRoom_Id(Long memberId, Long roomId);

  boolean existsByMember_IdAndRoom_Id(Long memberId, Long roomId);

  @Query(
      """
      SELECT new site.praytogether.pray_together.domain.member.model.MemberIdName(
        m.id, m.name
      )
      FROM MemberRoom mr
      JOIN Member m ON m.id = mr.member.id
      WHERE mr.room.id = :roomId
""")
  List<MemberIdName> findMember_IdAndNameByRoom_Id(Long roomId);

  @Query(
      """
      SELECT new site.praytogether.pray_together.domain.member_room.model.RoomInfo(
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
          SELECT new site.praytogether.pray_together.domain.member_room.model.RoomInfo(
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
        SELECT new site.praytogether.pray_together.domain.member_room.model.RoomIdMemberCount(
        mr.room.id, COUNT(*)
        )
        FROM MemberRoom mr
        WHERE mr.room.id IN :roomIds
        GROUP BY mr.room.id

""")
  List<RoomIdMemberCount> findMemberCountByIds(List<Long> roomIds);
}
