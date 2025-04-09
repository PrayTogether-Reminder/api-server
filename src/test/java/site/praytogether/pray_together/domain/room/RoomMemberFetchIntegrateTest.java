package site.praytogether.pray_together.domain.room;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.MemberIdName;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.room.dto.RoomCreateRequest;
import site.praytogether.pray_together.domain.room.dto.RoomMemberResponse;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("방 참가자 조회 통합 테스트")
public class RoomMemberFetchIntegrateTest extends IntegrateTest {

  private Member member;
  private HttpHeaders headers;
  private final String MEMBERS_URL = "/members";
  private Room room;
  private int memberCount = 10;

  @BeforeEach
  void setup() {
    // 회원 생성 및 JWT 설정
    member = testUtils.createUniqueMember();
    memberRepository.save(member);
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);

    // 방 생성
    RoomCreateRequest createRequest =
        RoomCreateRequest.builder().name("test-name").description("test-description").build();
    HttpEntity<RoomCreateRequest> requestEntity = new HttpEntity<>(createRequest, headers);
    restTemplate.postForEntity(ROOMS_API_URL, requestEntity, MessageResponse.class);

    // 방 정보 획득
    List<Room> allRoom = roomRepository.findAll();
    room = allRoom.get(0);

    // 방 참가자 생성 (본인 포함 총 memberCount 명)
    List<MemberRoom> memberRoomList = new ArrayList<>();
    List<Member> memberList = new ArrayList<>();
    for (int i = 0; i < memberCount - 1; i++) {
      Member newMember = testUtils.createUniqueMember();
      memberList.add(newMember);
      MemberRoom memberRoom =
          MemberRoom.builder()
              .member(newMember)
              .room(room)
              .role(RoomRole.MEMBER)
              .isNotification(true)
              .build();
      memberRoomList.add(memberRoom);
    }
    memberRepository.saveAll(memberList);
    memberRoomRepository.saveAll(memberRoomList);
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @Test
  @DisplayName("Room 참가자 조회 시 200 OK 응답")
  void fetch_room_members_then_return_200_ok() {

    // when
    // API 요청
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<RoomMemberResponse> responseEntity =
        restTemplate.exchange(
            ROOMS_API_URL + "/" + room.getId() + MEMBERS_URL,
            HttpMethod.GET,
            requestEntity,
            RoomMemberResponse.class);

    // when
    // 응답 상태 코드 검증
    assertThat(responseEntity.getStatusCode())
        .as("방 참가자 조회 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    RoomMemberResponse response = responseEntity.getBody();

    // 응답 결과 검증
    List<MemberIdName> members = response.getMembers();
    assertThat(members).as("방 참가자 조회 API 응답 결과가 NULL 입니다.").isNotNull();
    assertThat(members.size()).as("방 참가자 조회 API 응답 결과, 방 참가자 수가 예상과 다릅니다.").isEqualTo(memberCount);
    MemberIdName ownerMember =
        MemberIdName.builder().id(member.getId()).name(member.getName()).build();
    assertThat(members.contains(ownerMember)).as("방을 생성한 Member가 요청 방에 포함되지 않고 있습니다.").isTrue();
  }
}
