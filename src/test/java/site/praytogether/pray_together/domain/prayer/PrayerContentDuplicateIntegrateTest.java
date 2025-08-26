package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerContentCreateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.exception.ExceptionResponse;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 내용 중복 검증 통합 테스트")
public class PrayerContentDuplicateIntegrateTest extends IntegrateTest {

  private Member member;
  private Room room;
  private MemberRoom memberRoom;
  private HttpHeaders headers;
  private PrayerTitle prayerTitle;

  @BeforeEach
  void setup() {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    // 방 생성
    room = testUtils.createUniqueRoom();
    roomRepository.save(room);

    // 방 연관관계 생성
    memberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(member, room);
    memberRoomRepository.save(memberRoom);

    // 인증 헤더 생성
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);

    // 기도 제목 생성
    prayerTitle = PrayerTitle.create(room, "test-prayer-title");
    prayerTitleRepository.save(prayerTitle);
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @Test
  @DisplayName("같은 이름으로 기도 내용 중복 생성 시 400 Bad Request 응답")
  void create_duplicate_prayer_content_then_return_400_bad_request() {
    // given
    String memberName = "테스트사용자";
    
    // 첫 번째 기도 내용 생성
    PrayerContentCreateRequest firstRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(memberName)
        .content("첫 번째 기도 내용")
        .build();
    
    HttpEntity<PrayerContentCreateRequest> firstRequestEntity = new HttpEntity<>(firstRequest, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";
    
    // when - 첫 번째 요청
    ResponseEntity<MessageResponse> firstResponse = 
        restTemplate.postForEntity(url, firstRequestEntity, MessageResponse.class);
    
    // then - 첫 번째는 성공
    assertThat(firstResponse.getStatusCode())
        .as("첫 번째 기도 내용 생성은 성공해야 합니다.")
        .isEqualTo(HttpStatus.CREATED);
    
    // given - 같은 이름으로 두 번째 기도 내용 생성 시도
    PrayerContentCreateRequest secondRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(memberName)  // 같은 이름 사용
        .content("두 번째 기도 내용")
        .build();
    
    HttpEntity<PrayerContentCreateRequest> secondRequestEntity = new HttpEntity<>(secondRequest, headers);
    
    // when - 두 번째 요청
    ResponseEntity<ExceptionResponse> secondResponse = 
        restTemplate.postForEntity(url, secondRequestEntity, ExceptionResponse.class);
    
    // then - 두 번째는 실패
    assertThat(secondResponse.getStatusCode())
        .as("같은 이름으로 기도 내용 중복 생성 시 400 Bad Request가 반환되어야 합니다.")
        .isEqualTo(HttpStatus.BAD_REQUEST);
    
    ExceptionResponse exceptionResponse = secondResponse.getBody();
    assertThat(exceptionResponse).isNotNull();
    assertThat(exceptionResponse.getCode()).isEqualTo("PRAYER-003");
    assertThat(exceptionResponse.getMessage()).contains("이미 해당 기도 제목에 기도 내용을 작성하셨습니다");
  }

  @Test
  @DisplayName("다른 이름으로 기도 내용 생성 시 201 Created 응답")
  void create_prayer_content_with_different_name_then_return_201_created() {
    // given
    // 첫 번째 기도 내용 생성
    PrayerContentCreateRequest firstRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName("사용자1")
        .content("첫 번째 기도 내용")
        .build();
    
    HttpEntity<PrayerContentCreateRequest> firstRequestEntity = new HttpEntity<>(firstRequest, headers);
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";
    
    // when - 첫 번째 요청
    ResponseEntity<MessageResponse> firstResponse = 
        restTemplate.postForEntity(url, firstRequestEntity, MessageResponse.class);
    
    // then - 첫 번째는 성공
    assertThat(firstResponse.getStatusCode())
        .as("첫 번째 기도 내용 생성은 성공해야 합니다.")
        .isEqualTo(HttpStatus.CREATED);
    
    // given - 다른 이름으로 두 번째 기도 내용 생성
    PrayerContentCreateRequest secondRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName("사용자2")  // 다른 이름 사용
        .content("두 번째 기도 내용")
        .build();
    
    HttpEntity<PrayerContentCreateRequest> secondRequestEntity = new HttpEntity<>(secondRequest, headers);
    
    // when - 두 번째 요청
    ResponseEntity<MessageResponse> secondResponse = 
        restTemplate.postForEntity(url, secondRequestEntity, MessageResponse.class);
    
    // then - 두 번째도 성공
    assertThat(secondResponse.getStatusCode())
        .as("다른 이름으로 기도 내용 생성 시 성공해야 합니다.")
        .isEqualTo(HttpStatus.CREATED);
  }

  @Test
  @DisplayName("다른 기도 제목에는 같은 이름으로 기도 내용 생성 가능")
  void create_prayer_content_with_same_name_in_different_title_then_return_201_created() {
    // given
    String memberName = "테스트사용자";
    
    // 두 번째 기도 제목 생성
    PrayerTitle anotherPrayerTitle = PrayerTitle.create(room, "another-prayer-title");
    prayerTitleRepository.save(anotherPrayerTitle);
    
    // 첫 번째 기도 제목에 기도 내용 생성
    PrayerContentCreateRequest firstRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(memberName)
        .content("첫 번째 기도 내용")
        .build();
    
    HttpEntity<PrayerContentCreateRequest> firstRequestEntity = new HttpEntity<>(firstRequest, headers);
    String firstUrl = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents";
    
    // when - 첫 번째 기도 제목에 생성
    ResponseEntity<MessageResponse> firstResponse = 
        restTemplate.postForEntity(firstUrl, firstRequestEntity, MessageResponse.class);
    
    // then - 성공
    assertThat(firstResponse.getStatusCode())
        .as("첫 번째 기도 제목에 기도 내용 생성은 성공해야 합니다.")
        .isEqualTo(HttpStatus.CREATED);
    
    // given - 두 번째 기도 제목에 같은 이름으로 기도 내용 생성
    PrayerContentCreateRequest secondRequest = PrayerContentCreateRequest.builder()
        .memberId(member.getId())
        .memberName(memberName)  // 같은 이름 사용
        .content("두 번째 기도 내용")
        .build();
    
    HttpEntity<PrayerContentCreateRequest> secondRequestEntity = new HttpEntity<>(secondRequest, headers);
    String secondUrl = PRAYERS_API_URL + "/" + anotherPrayerTitle.getId() + "/contents";
    
    // when - 두 번째 기도 제목에 생성
    ResponseEntity<MessageResponse> secondResponse = 
        restTemplate.postForEntity(secondUrl, secondRequestEntity, MessageResponse.class);
    
    // then - 다른 기도 제목이므로 성공
    assertThat(secondResponse.getStatusCode())
        .as("다른 기도 제목에는 같은 이름으로 기도 내용 생성이 가능해야 합니다.")
        .isEqualTo(HttpStatus.CREATED);
  }
}