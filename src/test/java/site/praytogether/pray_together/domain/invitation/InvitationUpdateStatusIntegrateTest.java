package site.praytogether.pray_together.domain.invitation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private HttpHeaders headers;
  private Invitation invitation;

  @BeforeEach
  void setup() {
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
    headers = testUtils.create_Auth_HttpHeader_With_Member(memberInvitee);
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @ParameterizedTest(name = "[{index}] 초대장 상태를 {0}으로 변경하면 200 OK 응답")
  @MethodSource("provideValidInvitationStatusUpdateParameters")
  @DisplayName("초대장 상태 업데이트 시 200 OK 응답")
  void update_invitation_status_then_return_200_ok(String test, InvitationStatus updatedStatus) {
    // given
    InvitationStatusUpdateRequest request =
        InvitationStatusUpdateRequest.builder().status(updatedStatus).build();

    HttpEntity<InvitationStatusUpdateRequest> requestEntity = new HttpEntity<>(request, headers);
    String url = INVITATIONS_API_URL + "/" + invitation.getId();

    int memberRoomCnt = memberRoomRepository.findAll().size();

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("초대장 상태 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    // DB에서 초대장 확인
    Invitation updatedInvitation = invitationRepository.findById(invitation.getId()).orElseThrow();

    // 상태 확인
    assertThat(updatedInvitation.getStatus()).as("초대장 상태가 변경되지 않았습니다.").isEqualTo(updatedStatus);

    // 응답 시간 확인
    assertThat(updatedInvitation.getResponseTime()).as("초대장 응답 시간이 설정되지 않았습니다.").isNotNull();

    // 메시지 응답 확인
    MessageResponse response = responseEntity.getBody();
    assertThat(response).as("초대장 상태 변경 API 응답 결과가 NULL 입니다.").isNotNull();

    assertThat(response.getMessage())
        .as("초대장 상태 변경 API 응답 메시지가 예상과 다릅니다.")
        .contains(updatedStatus.getKoreanName());

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
      String test, Object invalidStatus) throws JsonProcessingException {

    // given
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("status", invalidStatus);
    String requestBody = objectMapper.writeValueAsString(requestMap);
    int memberRoomCnt = memberRoomRepository.findAll().size();

    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
    String url = INVITATIONS_API_URL + "/" + invitation.getId();

    // when
    ResponseEntity<ExceptionResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, ExceptionResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("잘못된 초대장 상태 변경 요청 시 400 Bad Request가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.BAD_REQUEST);

    ExceptionResponse response = responseEntity.getBody();
    System.out.println("response = " + response);
    assertThat(response.getStatus())
        .as("잘못된 초대장 상태 변경 요청 시 예외 응답에서 400 Bad Request가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.BAD_REQUEST.value());
    System.out.println("response.getMessage() = " + response.getMessage());
    System.out.println("response.getCode() = " + response.getCode());

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
      String test, InvitationStatus updatedStatus) {
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

    HttpEntity<InvitationStatusUpdateRequest> requestEntity = new HttpEntity<>(request, headers);
    String url = INVITATIONS_API_URL + "/" + invitation.getId();

    // when
    ResponseEntity<ExceptionResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, ExceptionResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("이미 수락한 초대장 상태 변경 요청 시 400 Bad Request가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.BAD_REQUEST);

    ExceptionResponse response = responseEntity.getBody();
    assertThat(response).as("예외 응답이 null입니다.").isNotNull();
    assertThat(response.getStatus())
        .as("이미 수락한 초대장 상태 변경 요청 시 예외 응답에서 400 Bad Request가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.BAD_REQUEST.value());

    // 에러 메시지 확인
    assertThat(response.getMessage()).as("이미 응답한 초대장에 대한 오류 메시지 Null 입니다..").isNotNull();

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
