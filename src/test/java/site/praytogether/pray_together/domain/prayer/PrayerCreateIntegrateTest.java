package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import site.praytogether.pray_together.domain.base.MessageResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.dto.PrayerCreateRequest;
import site.praytogether.pray_together.domain.prayer.model.PrayerContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerRequestContent;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("기도 작성 통합 테스트")
public class PrayerCreateIntegrateTest extends IntegrateTest {

  private Member member;
  private Room room;
  private MemberRoom memberRoom;
  private HttpHeaders headers;

  static Long validRoomId;
  static Long validMemberId;

  @BeforeEach
  void setup() {
    // 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);
    validMemberId = member.getId();

    // 방 생성
    room = testUtils.createUniqueRoom();
    roomRepository.save(room);
    validRoomId = room.getId();

    // 방 연관관계 생성
    memberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(member, room);
    memberRoomRepository.save(memberRoom);

    // 인증 헤더 생성
    headers = testUtils.create_Auth_HttpHeader_With_Member(member);
  }

  @AfterEach
  void cleanup() {
    cleanRepository();
  }

  @Test
  @DisplayName("기도(제목+내용) 생성 시 201 Created 응답")
  void create_prayer_with_valid_input_then_return_201_created() {

    // given
    List<Member> memberList = new ArrayList<>();
    // @Beforeach member 추가
    memberList.add(member);

    final int testCnt = 5;
    // 회원 추가 생성 - @Beforeach member 와 memberId == null 제외
    for (int i = 2; i < testCnt; i++) {
      memberList.add(testUtils.createUniqueMember());
    }
    memberRepository.saveAll(memberList);

    // 기도 내용 작성 - memberId == null 제외
    List<PrayerRequestContent> requestContents = new ArrayList<>();
    for (Member memberOne : memberList) {
      PrayerRequestContent content =
          PrayerRequestContent.builder()
              .memberId(memberOne.getId())
              .memberName(memberOne.getName())
              .content("test-prayer-content" + memberOne.getId())
              .build();
      requestContents.add(content);
    }

    // memberId == null 추가
    requestContents.add(
        PrayerRequestContent.builder()
            .memberName("test-memberName-id-null")
            .content("test-content-id-null")
            .build());

    // dto 생성
    PrayerCreateRequest requestDto =
        PrayerCreateRequest.builder()
            .title("test-prayer-title")
            .roomId(room.getId())
            .contents(requestContents)
            .build();

    HttpEntity<PrayerCreateRequest> requestEntity = new HttpEntity<>(requestDto, headers);

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.postForEntity(PRAYERS_API_URL, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 생성 API 응답 상태 코드가 201 Created가 아닙니다.")
        .isEqualTo(HttpStatus.CREATED);

    List<PrayerTitle> allTitle = prayerTitleRepository.findAll();
    assertThat(allTitle.size()).as("저장된 기도 제목의 개수가 예상과 다릅니다.").isEqualTo(1);

    List<PrayerContent> allContent = prayerContentRepository.findAll();
    assertThat(allContent.size()).as("저장된 기도 내용의 개수가 예상과 다릅니다.").isEqualTo(testCnt);
    allContent.forEach(content -> System.out.println(content.getId()));
  }

  @Test
  @DisplayName("기도(제목) 생성 시 201 Created 응답")
  void create_prayer_title_only_then_return_201_created() {
    // given
    PrayerCreateRequest requestDto =
        PrayerCreateRequest.builder()
            .title("test-prayer-title-only")
            .roomId(room.getId())
            .contents(Collections.emptyList())
            .build();

    HttpEntity<PrayerCreateRequest> requestEntity = new HttpEntity<>(requestDto, headers);

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.postForEntity(PRAYERS_API_URL, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as("기도 제목만 생성 API 응답 상태 코드가 201 Created가 아닙니다.")
        .isEqualTo(HttpStatus.CREATED);

    List<PrayerTitle> allTitle = prayerTitleRepository.findAll();
    assertThat(allTitle.size()).as("저장된 기도 제목의 개수가 예상과 다릅니다.").isEqualTo(1);
    assertThat(allTitle.get(0).getRoom().getId()).isEqualTo(this.room.getId());

    List<PrayerContent> allContent = prayerContentRepository.findAll();
    assertThat(allContent.size()).as("기도 제목만 생성 시 기도 내용은 저장되지 않아야 합니다.").isZero();
  }

  @DisplayName("기도 생성 요청 유효성 검사 실패 시 400 Bad Request 응답")
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("provideInvalidPrayerArguments")
  void create_prayer_with_invalid_input_then_return_400_bad_request(
      String test, Long roomId, String title, List<PrayerRequestContent> contents) {

    // given
    PrayerCreateRequest requestDto =
        PrayerCreateRequest.builder().roomId(roomId).title(title).contents(contents).build();
    HttpEntity<PrayerCreateRequest> requestEntity = new HttpEntity<>(requestDto, headers);

    // when
    ResponseEntity<MessageResponse> responseEntity =
        restTemplate.postForEntity(PRAYERS_API_URL, requestEntity, MessageResponse.class);

    // then
    assertThat(responseEntity.getStatusCode())
        .as(test + ": 응답 상태 코드가 400 Bad Request가 아닙니다.")
        .isEqualTo(HttpStatus.BAD_REQUEST);

    List<PrayerTitle> allTitle = prayerTitleRepository.findAll();
    assertThat(allTitle.size()).as(test + ": 예외 발생 시 기도 제목이 저장되면 안됩니다.").isZero();
    List<PrayerContent> allContent = prayerContentRepository.findAll();
    assertThat(allContent.size()).as(test + ": 예외 발생 시 기도 내용이 저장되면 안됩니다.").isZero();
  }

  private static Stream<Arguments> provideInvalidPrayerArguments() {
    String validTitle = "valid-title";
    String validMemberName = "valid-name";
    String validContentText = "valid-content";
    List<PrayerRequestContent> emptyContents = Collections.emptyList();

    return Stream.of(

        // --- roomId 유효성 검사 ---
        Arguments.of("roomId가 0일 때", 0L, validTitle, emptyContents),
        Arguments.of("roomId가 음수일 때", -1L, validTitle, emptyContents),
        Arguments.of("roomId가 null일 때", null, validTitle, emptyContents),

        // --- title 유효성 검사 ---
        Arguments.of("title ''일 때", validRoomId, "", emptyContents),
        Arguments.of("title이 null일 때", validRoomId, null, emptyContents),
        Arguments.of("title이 50자 초과일 때", validRoomId, "a".repeat(51), emptyContents),

        // --- contents (List size 1) 유효성 검사 ---
        Arguments.of(
            "contents의 content가 '' 일 때 (empty)",
            validRoomId,
            validTitle,
            List.of(
                PrayerRequestContent.builder()
                    .memberId(null)
                    .memberName(validMemberName)
                    .content("")
                    .build())),
        Arguments.of(
            "contents의 memberName이 '' 일 때 (empty)",
            validRoomId,
            validTitle,
            List.of(
                PrayerRequestContent.builder()
                    .memberId(null)
                    .memberName("")
                    .content(validContentText)
                    .build())),

        // --- contents 리스트 자체 유효성 검사 ---
        Arguments.of("contents 리스트가 null일 때", validRoomId, validTitle, null));
  }
}
