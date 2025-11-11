package site.praytogether.pray_together.domain.prayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
  private String token;
  private PrayerTitle prayerTitle;
  private List<PrayerContent> prayerContents;

  private static Long validMemberId;

  @BeforeEach
  void setup() throws Exception {
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
    token = testUtils.createBearerToken(member);

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

  @Test
  @DisplayName("기도 제목 변경 시 200 OK 응답")
  void update_prayer_title_then_return_200_ok() throws Exception {
    // given
    String newTitle = "updated-prayer-title";
    PrayerTitleUpdateRequest titleRequest = PrayerTitleUpdateRequest.builder()
        .changedTitle(newTitle)
        .build();

    String url = PRAYERS_API_URL + "/" + prayerTitle.getId();

    // when
    mockMvc.perform(put(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(titleRequest)))
        .andExpect(status().isOk());

    // then

    // 변경된 기도 제목 확인
    PrayerTitle updatedTitle =
        prayerTitleRepository.findByIdWithContents(prayerTitle.getId()).orElseThrow();
    assertThat(updatedTitle.getTitle()).as("기도 제목이 업데이트되지 않았습니다.").isEqualTo(newTitle);
  }

  @Test
  @DisplayName("기도 내용 변경 시 200 OK 응답")
  void update_prayer_content_then_return_200_ok() throws Exception {
    // given
    String newContent = "updated-prayer-content";
    PrayerContentUpdateRequest contentRequest = PrayerContentUpdateRequest.builder()
        .changedContent(newContent)
        .build();

    Long contentId = prayerContents.get(0).getId();
    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents/" + contentId;

    // when
    mockMvc.perform(put(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(contentRequest)))
        .andExpect(status().isOk());

    // then

    // 변경된 기도 내용 확인
    PrayerContent updatedContent = prayerContentRepository.findById(contentId).orElseThrow();
    assertThat(updatedContent.getContent())
        .as("기도 내용이 업데이트되지 않았습니다.")
        .isEqualTo(newContent);
  }

  @Test
  @DisplayName("다른 회원이 기도 내용 수정 시 writer 정보가 변경됨")
  void when_different_member_updates_content_then_writer_info_changes() throws Exception {
    // given - 다른 회원 생성 및 방에 추가
    Member anotherMember = testUtils.createUniqueMember();
    memberRepository.save(anotherMember);
    MemberRoom anotherMemberRoom = testUtils.createUniqueMemberRoom_With_Member_AND_Room(anotherMember, room);
    memberRoomRepository.save(anotherMemberRoom);

    // 다른 회원의 인증 헤더 생성
    String anotherToken = testUtils.createBearerToken(anotherMember);

    String newContent = "content-updated-by-another-member";
    PrayerContentUpdateRequest contentRequest = PrayerContentUpdateRequest.builder()
        .changedContent(newContent)
        .build();

    Long contentId = prayerContents.get(0).getId();

    // 원래 writer 정보 확인
    PrayerContent originalContent = prayerContentRepository.findById(contentId).orElseThrow();
    assertThat(originalContent.getWriterId()).isEqualTo(member.getId());
    assertThat(originalContent.getWriterName()).isEqualTo(member.getName());

    PrayerContentUpdateRequest  contentUpdateRequest = PrayerContentUpdateRequest.builder()
        .changedContent(newContent).build();

    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents/" + contentId;

    // when - 다른 회원이 기도 내용 수정
    mockMvc.perform(put(url)
            .header(HttpHeaders.AUTHORIZATION, anotherToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(contentUpdateRequest)))
        .andExpect(status().isOk());

    // then

    // writer 정보가 수정한 회원으로 변경되었는지 확인
    PrayerContent updatedContent = prayerContentRepository.findById(contentId).orElseThrow();
    assertThat(updatedContent.getWriterId())
        .as("다른 회원이 수정 시 writerId가 변경되지 않았습니다.")
        .isEqualTo(anotherMember.getId());
    assertThat(updatedContent.getWriterName())
        .as("다른 회원이 수정 시 writerName이 변경되지 않았습니다.")
        .isEqualTo(anotherMember.getName());
    assertThat(updatedContent.getContent())
        .as("기도 내용이 업데이트되지 않았습니다.")
        .isEqualTo(newContent);
  }


  @Test
  @DisplayName("존재하지 않는 기도 제목 ID로 제목 변경 요청 시 404 Not Found 응답")
  void update_prayer_title_with_nonexistent_id_then_return_404_not_found() throws Exception {
    // given
    Long nonExistentId = 999999L;
    PrayerTitleUpdateRequest titleRequest = PrayerTitleUpdateRequest.builder()
        .changedTitle("updated-prayer-title")
        .build();

    String url = PRAYERS_API_URL + "/titles/" + nonExistentId;

    // when & then
    mockMvc.perform(put(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(titleRequest)))
        .andExpect(status().isNotFound());
  }
  
  @Test
  @DisplayName("존재하지 않는 기도 내용 ID로 내용 변경 요청 시 404 Not Found 응답")
  void update_prayer_content_with_nonexistent_id_then_return_404_not_found() throws Exception {
    // given
    Long nonExistentContentId = 999999L;
    PrayerContentUpdateRequest contentRequest = PrayerContentUpdateRequest.builder()
        .changedContent("updated-content")
        .build();

    String url = PRAYERS_API_URL + "/" + prayerTitle.getId() + "/contents/" + nonExistentContentId;

    // when & then
    mockMvc.perform(put(url)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(contentRequest)))
        .andExpect(status().isNotFound());
  }
}
