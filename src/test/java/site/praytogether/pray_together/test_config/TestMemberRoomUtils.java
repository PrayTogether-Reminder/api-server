package site.praytogether.pray_together.test_config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.member_room.repository.MemberRoomRepository;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;

/**
 * 멤버와 방 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestMemberRoomUtils {

  private final MemberRoomRepository memberRoomRepository;
  private final TestMemberUtils testMemberUtils;

  /**
   * 여러 멤버를 생성하고 방에 참여시킴
   *
   * @param room 멤버들이 참여할 방
   * @param count 생성할 멤버 수
   * @return 생성된 멤버 배열
   */
  public Member[] createMembersAndJoinRoom(Room room, int count) {
    Member[] members = new Member[count];
    for (int i = 0; i < count; i++) {
      members[i] = testMemberUtils.createSave();

      createSaveMemberRoom_With_Member_AND_Room(members[i], room);
    }
    return members;
  }

  public MemberRoom createSaveMemberRoom_With_MemberOwner_AND_Room(Member member, Room room) {
    MemberRoom memberRoom = MemberRoom.builder()
        .member(member)
        .room(room)
        .role(RoomRole.OWNER)
        .isNotification(true)
        .build();
    return  memberRoomRepository.save(memberRoom);
  }

  public MemberRoom createSaveMemberRoom_With_Member_AND_Room(Member member, Room room) {
    MemberRoom memberRoom = MemberRoom.builder()
        .member(member)
        .room(room)
        .role(RoomRole.MEMBER)
        .isNotification(true)
        .build();
    return memberRoomRepository.save(memberRoom);
  }
}
