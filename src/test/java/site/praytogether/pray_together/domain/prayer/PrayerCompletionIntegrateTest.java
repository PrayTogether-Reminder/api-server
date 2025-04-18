package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.web.util.UriComponentsBuilder;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.notification.constant.NotificationMessageFormat;
import site.praytogether.pray_together.domain.notification.model.PrayerCompletionNotification;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCompletionCreateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerCompletion;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 완료 처리 통합 테스트")
public class PrayerCompletionIntegrateTest extends IntegrateTest {

  private Member member;
  private HttpHeaders headers;
  private Room room;
  private PrayerTitle prayerTitle;
  private final String COMPLETION_URL_FORMAT = "/{prayerTitleId}/completion";

  // 추가 회원 생성
  private final int ADDITIONAL_MEMBERS_COUNT = 3;
  private Member[] additionalMembers;

  @BeforeEach
  void setup() {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    // 방 생성
    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    // 회원-방 연관관계 생성
    MemberRoom memberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(member, room);
    memberRoomRepository.save(memberRoom);

    // 기도 제목 생성
    prayerTitle = testUtils.createUniquePrayerTitle_With_Room(room);
    prayerTitleRepository.save(prayerTitle);

    // 추가 회원 생성 및 방에 참여시키기
    additionalMembers = new Member[ADDITIONAL_MEMBERS_COUNT];
    for (int i = 0; i < ADDITIONAL_MEMBERS_COUNT; i++) {
      additionalMembers[i] = testUtils.createUniqueMember();
      memberRepository.save(additionalMembers[i]);

      MemberRoom additionalMemberRoom =
          testUtils.createUniqueMemberRoom_With_Member_AND_Room(additionalMembers[i], room);
      memberRoomRepository.save(additionalMemberRoom);
    }

    // 인증 헤더 생성
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @Test
  @DisplayName("기도 완료 처리 시 200 OK 응답 및 알림 생성 확인")
  void complete_prayer_then_create_notifications_and_return_200_ok() {

    // given
    String uri =
        UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .path(COMPLETION_URL_FORMAT)
            .buildAndExpand(prayerTitle.getId())
            .toUriString();

    PrayerCompletionCreateRequest request =
        PrayerCompletionCreateRequest.builder().roomId(room.getId()).build();

    HttpEntity<PrayerCompletionCreateRequest> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(uri, HttpMethod.POST, requestEntity, MessageResponse.class);

    // then
    // 응답 검증
    assertThat(responseEntity.getStatusCode())
        .as("기도 완료 처리 API 응답 상태 코드가 200 OK이 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    MessageResponse response = responseEntity.getBody();
    assertThat(response).as("기도 완료 처리 API 응답 결과가 NULL 입니다.").isNotNull();

    // 기도 완료 엔티티 생성 검증
    List<PrayerCompletion> completions = prayerCompletionRepository.findAll();
    assertThat(completions).as("기도 완료 정보가 저장되지 않았습니다.").isNotEmpty();

    assertThat(completions.size()).as("기도 완료 정보 개수가 예상과 다릅니다.").isEqualTo(1);

    PrayerCompletion completion = completions.get(0);
    assertThat(completion.getPrayerId())
        .as("기도 완료 정보의 기도자 ID가 예상과 다릅니다.")
        .isEqualTo(member.getId());

    assertThat(completion.getPrayerTitle().getId())
        .as("기도 완료 정보의 기도 제목 ID가 예상과 다릅니다.")
        .isEqualTo(prayerTitle.getId());

    // 알림 생성 검증
    List<PrayerCompletionNotification> notifications =
        prayerCompletionNotificationRepository.findAll();
    assertThat(notifications).as("기도 완료 알림이 생성되지 않았습니다.").isNotEmpty();

    assertThat(notifications.size())
        .as("생성된 알림 개수가 예상과 다릅니다. (알림은 자신을 제외한 다른 멤버들에게만 전송됨)")
        .isEqualTo(ADDITIONAL_MEMBERS_COUNT);

    for (PrayerCompletionNotification notification : notifications) {
      assertThat(notification.getSenderId()).as("알림의 발신자 ID가 예상과 다릅니다.").isEqualTo(member.getId());

      assertThat(notification.getPrayerTitleId())
          .as("알림의 기도 제목 ID가 예상과 다릅니다.")
          .isEqualTo(prayerTitle.getId());

      assertThat(notification.getMessage()).as("알림의 메시지가 NULL입니다.").isNotNull();

      // 알림 메시지 형식 검증
      String expectedMessage =
          String.format(
              NotificationMessageFormat.PrayerCompletion, member.getName(), prayerTitle.getTitle());
      assertThat(notification.getMessage()).as("알림 메시지가 예상 형식과 다릅니다.").isEqualTo(expectedMessage);
    }
  }
}
