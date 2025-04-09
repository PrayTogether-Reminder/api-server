package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
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
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerUpdateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.model.PrayerRequestContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.prayer.model.PrayerUpdateContent;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 변경 통합 테스트")
public class PrayerUpdateIntegrateTest extends IntegrateTest {

  private static final int TEST_CNT = 5;

  private Member member;
  private Room room;
  private MemberRoom memberRoom;
  private HttpHeaders headers;
  private PrayerTitle prayerTitle;
  private List<PrayerContent> prayerContents;

  private static Long validMemberId;

  @BeforeEach
  void setup() {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);
    validMemberId = member.getId();

    // 방 생성
    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    // 방 연관관계 생성
    memberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(member, room);
    memberRoomRepository.save(memberRoom);

    // 인증 헤더 생성
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);

    // 기도 제목 생성
    prayerTitle = PrayerTitle.create(room, "original-prayer-title");
    prayerTitleRepository.save(prayerTitle);

    // 기도 내용 생성
    prayerContents = new ArrayList<>();
    for (int i = 0; i < TEST_CNT; i++) {
      PrayerContent content =
          PrayerContent.create(
              prayerTitle,
              PrayerRequestContent.builder()
                  .memberId(member.getId())
                  .memberName(member.getName())
                  .content("original-prayer-content-" + i)
                  .build());
      prayerTitle.addContent(content);
      prayerContentRepository.save(content);
      prayerContents.add(content);
    }
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @Test
  @DisplayName("기도(제목+내용) 변경 시 200 OK 응답")
  void update_prayer_then_return_200_ok() {
    // given
    String newTitle = "updated-prayer-title";
    String newContent = "updated-prayer-content";

    List<PrayerUpdateContent> updateContents = new ArrayList<>();
    updateContents.add(
        PrayerUpdateContent.builder()
            .id(prayerContents.get(0).getId())
            .memberId(member.getId())
            .memberName(member.getName())
            .content(newContent)
            .build());

    PrayerUpdateRequest requestDto =
        PrayerUpdateRequest.builder().title(newTitle).contents(updateContents).build();

    HttpEntity<PrayerUpdateRequest> requestEntity = new HttpEntity<>(requestDto, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    // 변경된 기도 제목 확인
    PrayerTitle updatedTitle =
        prayerTitleRepository.findByIdWithContents(prayerTitle.getId()).orElseThrow();
    assertThat(updatedTitle.getTitle()).as("기도 제목이 업데이트되지 않았습니다.").isEqualTo(newTitle);

    // 변경된 기도 내용 확인
    List<PrayerContentInfo> updatedContents =
        prayerContentRepository.findPrayerContentsByTitleId(prayerTitle.getId());
    assertThat(updatedContents.size()).as("변경된 기도 내용의 개수가 예상과 다릅니다.").isEqualTo(1);
    assertThat(updatedContents.get(0).getContent())
        .as("기도 내용이 업데이트되지 않았습니다.")
        .isEqualTo(newContent);
  }

  @Test
  @DisplayName("기도 내용 추가 변경 시 200 OK 응답")
  void update_prayer_add_content_then_return_200_ok() {
    // given
    String newTitle = "updated-prayer-title";
    String existingContent = "existing-content";
    String newContent = "new-content";

    // 기존 content 업데이트
    List<PrayerUpdateContent> updateContents = new ArrayList<>();
    updateContents.add(
        PrayerUpdateContent.builder()
            .id(prayerContents.get(0).getId())
            .memberId(member.getId())
            .memberName(member.getName())
            .content(existingContent)
            .build());

    // 새로운 content 추가
    updateContents.add(
        PrayerUpdateContent.builder()
            .memberId(member.getId())
            .memberName(member.getName())
            .content(newContent)
            .build());

    PrayerUpdateRequest requestDto =
        PrayerUpdateRequest.builder().title(newTitle).contents(updateContents).build();

    HttpEntity<PrayerUpdateRequest> requestEntity = new HttpEntity<>(requestDto, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    // 변경된 기도 제목 확인
    PrayerTitle updatedTitle =
        prayerTitleRepository.findByIdWithContents(prayerTitle.getId()).orElseThrow();
    assertThat(updatedTitle.getTitle()).as("기도 제목이 업데이트되지 않았습니다.").isEqualTo(newTitle);

    // 변경된 기도 내용 확인
    List<PrayerContentInfo> updatedContents =
        prayerContentRepository.findPrayerContentsByTitleId(prayerTitle.getId());
    assertThat(updatedContents.size()).as("변경된 기도 내용의 개수가 예상과 다릅니다.").isEqualTo(2);

    // 내용 확인
    boolean hasExistingContent =
        updatedContents.stream().anyMatch(content -> content.getContent().equals(existingContent));
    boolean hasNewContent =
        updatedContents.stream().anyMatch(content -> content.getContent().equals(newContent));

    assertThat(hasExistingContent).as("기존 기도 내용이 업데이트되지 않았습니다.").isTrue();
    assertThat(hasNewContent).as("새로운 기도 내용이 추가되지 않았습니다.").isTrue();
  }

  @Test
  @DisplayName("기도 내용 삭제 변경 시 200 OK 응답")
  void update_prayer_remove_content_then_return_200_ok() {
    // given
    String newTitle = "updated-prayer-title";

    // 빈 내용 목록으로 업데이트 (기존 내용 삭제)
    List<PrayerUpdateContent> updateContents = Collections.emptyList();

    PrayerUpdateRequest requestDto =
        PrayerUpdateRequest.builder().title(newTitle).contents(updateContents).build();

    HttpEntity<PrayerUpdateRequest> requestEntity = new HttpEntity<>(requestDto, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    // 변경된 기도 제목 확인
    PrayerTitle updatedTitle = prayerTitleRepository.findById(prayerTitle.getId()).orElseThrow();
    assertThat(updatedTitle.getTitle()).as("기도 제목이 업데이트되지 않았습니다.").isEqualTo(newTitle);

    // 변경된 기도 내용 확인
    List<PrayerContentInfo> updatedContents =
        prayerContentRepository.findPrayerContentsByTitleId(prayerTitle.getId());
    assertThat(updatedContents.size()).as("기도 내용이 모두 삭제되지 않았습니다.").isZero();
  }

  @Test
  @DisplayName("존재하지 않는 기도 제목 ID로 변경 요청 시 404 Not Found 응답")
  void update_prayer_with_nonexistent_id_then_return_404_not_found() {
    // given
    Long nonExistentId = 999999L;

    PrayerUpdateRequest requestDto =
        PrayerUpdateRequest.builder()
            .title("updated-prayer-title")
            .contents(Collections.emptyList())
            .build();

    HttpEntity<PrayerUpdateRequest> requestEntity = new HttpEntity<>(requestDto, headers);
    String url = PRAYERS_API_URL + "/" + nonExistentId;

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("존재하지 않는 기도 제목 ID로 변경 요청 시 404 Not Found가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
