package site.praytogether.pray_together.domain.invitation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.invitation.dto.InvitationStatusUpdateRequest;
import site.praytogether.pray_together.domain.invitation.model.Invitation;
import site.praytogether.pray_together.domain.invitation.model.InvitationStatus;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.exception.ExceptionResponse;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("방 초대 상태 변경 통합 테스트")
public class InvitationUpdateStatusIntegrateTest extends IntegrateTest {
  private Member memberInviter;
  private Member memberInvitee;
  private Room room;
  private String token;
  private Invitation invitation;

  @BeforeEach
  void setup() throws Exception {
    // 초대자 회원 생성
    memberInviter = testUtils.createUniqueMember();
    memberRepository.save(memberInviter);

    // 초대받는 회원 생성
    memberInvitee = testUtils.createUniqueMember();
    memberRepository.save(memberInvitee);

    // 방 생성
    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    // 초대자-방 연관관계 생성
    MemberRoom memberRoom =
        testUtils.createUniqueMemberRoom_With_Member_AND_Room(memberInviter, room);
    memberRoomRepository.save(memberRoom);

    // 초대장 생성
    invitation = Invitation.create(memberInviter, memberInvitee, room);
    invitationRepository.save(invitation);

    // 생성된 초대장 조회
    invitation = invitationRepository.findAll().get(0);

    // 테스트용 인증 헤더 설정
    token = testUtils.createBearerToken(memberInvitee);
  }

  @ParameterizedTest(name = "[{index}] 초대장 상태를 {0}으로 변경하면 200 OK 응답")
  @MethodSource("provideValidInvitationStatusUpdateParameters")
  @DisplayName("초대장 상태 업데이트 시 200 OK 응답")
  void update_invitation_status_then_return_200_ok(String test, InvitationStatus updatedStatus) throws Exception {
    // given
    InvitationStatusUpdateRequest request =
        InvitationStatusUpdateRequest.builder().status(updatedStatus).build();

    String url = INVITATIONS_API_URL + "/" + invitation.getId();

    int memberRoomCnt = memberRoomRepository.findAll().size();

    // when & then
    mockMvc.perform(patch(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString(updatedStatus.getKoreanName())));

    // DB에서 초대장 확인
    Invitation updatedInvitation = invitationRepository.findById(invitation.getId()).orElseThrow();

    // 상태 확인
    assertThat(updatedInvitation.getStatus()).as("초대장 상태가 변경되지 않았습니다.").isEqualTo(updatedStatus);

    // 응답 시간 확인
    assertThat(updatedInvitation.getResponseTime()).as("초대장 응답 시간이 설정되지 않았습니다.").isNotNull();

    // 방 참가 인원 수 확인
    List<MemberRoom> allMemberRoom = memberRoomRepository.findAll();
    if (updatedStatus == InvitationStatus.ACCEPTED) {
      // 방 인원 증가
      assertThat(allMemberRoom.size())
          .as("초대장 수락시 방 참가 인원이 달라져야(1 증가해야) 합니다.")
          .isEqualTo(memberRoomCnt + 1);

    } else if (updatedStatus == InvitationStatus.REJECTED) {
      // 방 인원 유지
      assertThat(allMemberRoom.size())
          .as("초대장 수락시 방 참가 인원이 이전과 동일해야 합니다.")
          .isEqualTo(memberRoomCnt);
    }
  }

  private static Stream<Arguments> provideValidInvitationStatusUpdateParameters() {
    return Stream.of(
        Arguments.of("수락", InvitationStatus.ACCEPTED),
        Arguments.of("거절", InvitationStatus.REJECTED));
  }

  @ParameterizedTest(name = "[{index}] {0}인 경우 400 Bad Request 응답")
  @MethodSource("provideInvalidInvitationStatusUpdateParameters")
  @DisplayName("초대장 상태 업데이트 시 잘못된 값이면 400 Bad Request 응답")
  void update_invitation_status_with_invalid_input_then_return_400_bad_request(
      String test, Object invalidStatus) throws Exception {

    // given
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("status", invalidStatus);
    String requestBody = objectMapper.writeValueAsString(requestMap);
    int memberRoomCnt = memberRoomRepository.findAll().size();

    String url = INVITATIONS_API_URL + "/" + invitation.getId();

    // when & then
    mockMvc.perform(patch(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());

    // DB에서 초대장 확인 - 상태가 변경되지 않아야 함
    Invitation unchangedInvitation =
        invitationRepository.findById(invitation.getId()).orElseThrow();

    // 상태가 PENDING 유지 확인
    assertThat(unchangedInvitation.getStatus())
        .as("잘못된 요청에도 초대장 상태가 변경되었습니다.")
        .isEqualTo(InvitationStatus.PENDING);

    // 응답 시간이 여전히 null인지 확인
    assertThat(unchangedInvitation.getResponseTime()).as("잘못된 요청에도 초대장 응답 시간이 설정되었습니다.").isNull();

    // 아무도 방에 초대되지 않음
    List<MemberRoom> allMemberRoom = memberRoomRepository.findAll();
    assertThat(allMemberRoom.size()).isEqualTo(memberRoomCnt);
  }

  private static Stream<Arguments> provideInvalidInvitationStatusUpdateParameters() {
    return Stream.of(
        Arguments.of("status가 빈 문자열", ""),
        Arguments.of("status가 소문자 accepted", "accepted"),
        Arguments.of("status가 소문자 rejected", "rejected"),
        Arguments.of("status가 존재하지 않는 값", "UNKNOWN_STATUS"),
        Arguments.of("status가 숫자", 123),
        Arguments.of("status가 불리언", true),
        Arguments.of("status가 null", null));
  }

  @ParameterizedTest(name = "[{index}] 이미 수락한 초대장을 {0}(으)로 변경 시도하면 400 Bad Request 응답")
  @MethodSource("provideAlreadyAcceptedInvitationParameters")
  @DisplayName("이미 수락한 초대장 상태 변경 시 400 Bad Request 응답")
  void update_already_accepted_invitation_status_then_return_400_bad_request(
      String test, InvitationStatus updatedStatus) throws Exception {
    // given
    invitation.accept();
    invitationRepository.save(invitation);

    // 이미 응답 시간이 설정되어 있는지 확인
    assertThat(invitationRepository.findById(invitation.getId()).orElseThrow().getResponseTime())
        .as("초대장 응답 시간이 설정되지 않았습니다.")
        .isNotNull();

    // 다시 상태 변경 요청
    InvitationStatusUpdateRequest request =
        InvitationStatusUpdateRequest.builder().status(updatedStatus).build();

    String url = INVITATIONS_API_URL + "/" + invitation.getId();

    // when & then
    mockMvc.perform(patch(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").exists());

    // DB에서 초대장 확인 - 상태가 변경되지 않아야 함
    Invitation unchangedInvitation =
        invitationRepository.findById(invitation.getId()).orElseThrow();

    // 상태가 ACCEPTED 그대로 유지되는지 확인
    assertThat(unchangedInvitation.getStatus())
        .as("이미 수락한 초대장의 상태가 변경되었습니다.")
        .isEqualTo(InvitationStatus.ACCEPTED);
  }

  private static Stream<Arguments> provideAlreadyAcceptedInvitationParameters() {
    return Stream.of(
        Arguments.of("수락", InvitationStatus.ACCEPTED),
        Arguments.of("거절", InvitationStatus.REJECTED));
  }
}
