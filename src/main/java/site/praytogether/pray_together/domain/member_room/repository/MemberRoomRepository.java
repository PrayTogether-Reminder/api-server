package site.praytogether.pray_together.domain.member_room.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.praytogether.pray_together.domain.member.model.RoomMember;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.member_room.model.RoomIdMemberCount;
import site.praytogether.pray_together.domain.member_room.model.RoomInfo;

public interface MemberRoomRepository extends JpaRepository<MemberRoom, Long> {

  int deleteByMember_IdAndRoom_Id(Long memberId, Long roomId);

  boolean existsByMember_IdAndRoom_Id(Long memberId, Long roomId);

  Optional<MemberRoom> findByMember_IdAndRoom_Id(Long memberId, Long roomId);

  @Query(
      """
      SELECT new site.praytogether.pray_together.domain.member.model.RoomMember(
        m.id, m.name,m.phoneNumber
      )
      FROM MemberRoom mr
      JOIN Member m ON m.id = mr.member.id
      WHERE mr.room.id = :roomId
      ORDER BY m.name ASC
""")
  List<RoomMember> findRoomMembers(Long roomId);

  @Query(
      """
    SELECT mr.member.id
    FROM MemberRoom mr
    WHERE mr.room.id = :roomId

""")
  List<Long> findMember_IdByRoom_Id(Long roomId);

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


  @Query(
      """
      SELECT CASE WHEN COUNT(DISTINCT mr.member.id) > 0 THEN true ELSE false END 
      FROM MemberRoom mr
      WHERE mr.member.id IN :memberIds
      AND mr.room.id = :roomId
"""
  )
  boolean isExistingMembersInRoom(@Param("memberIds") List<Long> memberIds,@Param("roomId") Long roomId);
}
