package site.praytogether.pray_together.test_config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.member_room.repository.MemberRoomRepository;
import site.praytogether.pray_together.domain.room.model.Room;

/**
 * 멤버와 방 관련 테스트 헬퍼 유틸리티
 */
@Component
@RequiredArgsConstructor
public class TestMemberRoomUtils {

  private final MemberRepository memberRepository;
  private final MemberRoomRepository memberRoomRepository;
  private final TestUtils testUtils;

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
      members[i] = testUtils.createUniqueMember();
      memberRepository.save(members[i]);

      MemberRoom memberRoom =
          testUtils.createUniqueMemberRoom_With_Member_AND_Room(members[i], room);
      memberRoomRepository.save(memberRoom);
    }
    return members;
  }

  /**
   * 멤버 삭제 (회원 탈퇴 시뮬레이션)
   *
   * @param member 삭제할 멤버
   */
  public void deleteMember(Member member) {

  }
}
