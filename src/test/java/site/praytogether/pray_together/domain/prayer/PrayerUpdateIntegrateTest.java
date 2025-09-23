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
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentUpdateRequest;
import site.praytogether.pray_together.domain.prayer.dto.PrayerTitleUpdateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerContentInfo;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
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
    prayerTitle = PrayerTitle.create(room, "original-prayer-changedTitle");
    prayerTitleRepository.save(prayerTitle);

    // 기도 내용 생성 - 각각 다른 이름으로 생성
    prayerContents = new ArrayList<>();
    for (int i = 0; i < TEST_CNT; i++) {
      PrayerContent content =
          PrayerContent.create(
              prayerTitle,
              PrayerContentCreateRequest.builder()
                  .memberId(member.getId())
                  .memberName(member.getName() + "-" + i)  // 각각 다른 이름 사용
                  .content("original-prayer-content-" + i)
                  .build(),
              member);
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
  @DisplayName("기도 제목 변경 시 200 OK 응답")
  void update_prayer_title_then_return_200_ok() {
    // given
    String newTitle = "updated-prayer-title";
    PrayerTitleUpdateRequest titleRequest = PrayerTitleUpdateRequest.builder()
        .changedTitle(newTitle)
        .build();
    
    HttpEntity<PrayerTitleUpdateRequest> requestEntity = new HttpEntity<>(titleRequest, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 제목 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    // 변경된 기도 제목 확인
    PrayerTitle updatedTitle =
        prayerTitleRepository.findByIdWithContents(prayerTitle.getId()).orElseThrow();
    assertThat(updatedTitle.getTitle()).as("기도 제목이 업데이트되지 않았습니다.").isEqualTo(newTitle);
  }

  @Test
  @DisplayName("기도 내용 변경 시 200 OK 응답")
  void update_prayer_content_then_return_200_ok() {
    // given
    String newContent = "updated-prayer-content";
    PrayerContentUpdateRequest contentRequest = PrayerContentUpdateRequest.builder()
        .changedContent(newContent)
        .build();
    
    Long contentId = prayerContents.get(0).getId();
    HttpEntity<PrayerContentUpdateRequest> requestEntity = new HttpEntity<>(contentRequest, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents/" + contentId;

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 내용 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);

    // 변경된 기도 내용 확인
    PrayerContent updatedContent = prayerContentRepository.findById(contentId).orElseThrow();
    assertThat(updatedContent.getContent())
        .as("기도 내용이 업데이트되지 않았습니다.")
        .isEqualTo(newContent);
  }

  @Test
  @DisplayName("기도 내용 생성 시 201 CREATED 응답")
  void create_prayer_content_then_return_201_created() {
    // given
    String newContent = "new-prayer-content";
    PrayerContentCreateRequest contentRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(member.getName() + "-new")  // 새로운 이름 사용
        .content(newContent)
        .build();
    
    HttpEntity<PrayerContentCreateRequest> requestEntity = new HttpEntity<>(contentRequest, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";
    
    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, MessageResponse.class);
    
    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 내용 생성 API 응답 상태 코드가 201 CREATED가 아닙니다.")
        .isEqualTo(HttpStatus.CREATED);
    
    // 생성된 기도 내용 확인
    List<PrayerContentInfo> contents =
        prayerContentRepository.findPrayerContentsByTitleId(prayerTitle.getId());
    assertThat(contents.size()).as("기도 내용 개수가 예상과 다릅니다.").isEqualTo(TEST_CNT + 1);
    
    boolean hasNewContent = contents.stream()
        .anyMatch(content -> content.getContent().equals(newContent));
    assertThat(hasNewContent).as("새로운 기도 내용이 추가되지 않았습니다.").isTrue();
  }

  @Test
  @DisplayName("기도 내용 삭제 시 200 OK 응답")
  void delete_prayer_content_then_return_200_ok() {
    // given
    Long contentId = prayerContents.get(0).getId();
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents/" + contentId;
    
    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, MessageResponse.class);
    
    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 내용 삭제 API 응답 상태 코드가 200 OK가 아닙니다.")
        .isEqualTo(HttpStatus.OK);
    
    // 삭제 확인
    boolean exists = prayerContentRepository.existsById(contentId);
    assertThat(exists).as("기도 내용이 삭제되지 않았습니다.").isFalse();
    
    // 남은 내용 개수 확인
    List<PrayerContentInfo> remainingContents =
        prayerContentRepository.findPrayerContentsByTitleId(prayerTitle.getId());
    assertThat(remainingContents.size()).as("남은 기도 내용 개수가 예상과 다릅니다.").isEqualTo(TEST_CNT - 1);
  }

  @Test
  @DisplayName("존재하지 않는 기도 제목 ID로 제목 변경 요청 시 404 Not Found 응답")
  void update_prayer_title_with_nonexistent_id_then_return_404_not_found() {
    // given
    Long nonExistentId = 999999L;
    PrayerTitleUpdateRequest titleRequest = PrayerTitleUpdateRequest.builder()
        .changedTitle("updated-prayer-title")
        .build();

    HttpEntity<PrayerTitleUpdateRequest> requestEntity = new HttpEntity<>(titleRequest, headers);
    String url = PRAYERS_API_URL + "/titles/" + nonExistentId;

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("존재하지 않는 기도 제목 ID로 변경 요청 시 404 Not Found가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
  
  @Test
  @DisplayName("존재하지 않는 기도 내용 ID로 내용 변경 요청 시 404 Not Found 응답")
  void update_prayer_content_with_nonexistent_id_then_return_404_not_found() {
    // given
    Long nonExistentContentId = 999999L;
    PrayerContentUpdateRequest contentRequest = PrayerContentUpdateRequest.builder()
        .changedContent("updated-content")
        .build();

    HttpEntity<PrayerContentUpdateRequest> requestEntity = new HttpEntity<>(contentRequest, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents/" + nonExistentContentId;

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.exchange(url, HttpMethod.PUT, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("존재하지 않는 기도 내용 ID로 변경 요청 시 404 Not Found가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
