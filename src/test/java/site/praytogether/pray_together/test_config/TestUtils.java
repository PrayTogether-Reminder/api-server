package site.praytogether.pray_together.test_config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import site.praytogether.pray_together.domain.auth.model.PrayTogetherPrincipal;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.domain.member.model.PhoneNumber;
import site.praytogether.pray_together.domain.member_room.model.MemberRoom;
import site.praytogether.pray_together.domain.prayer.model.PrayerTitle;
import site.praytogether.pray_together.domain.room.model.Room;
import site.praytogether.pray_together.domain.room.model.RoomRole;
import site.praytogether.pray_together.security.service.JwtService;

@Component
@RequiredArgsConstructor
public class TestUtils {

  private static int emailUniqueId = 0;
  private static int roomUniqueId = 0;
  private static int prayerTitleUniqueId = 0;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  public Member createUniqueMember() {
    return Member.create("test" + (emailUniqueId), "test@test.com" + (emailUniqueId++), "test", PhoneNumber.of("010-1234-5678"));
  }

  public PrayerTitle createUniquePrayerTitle_With_Room(Room room) {
    return PrayerTitle.create(room, "test-prayer-changedTitle" + prayerTitleUniqueId++);
  }

  public MemberRoom createUniqueMemberRoom_With_Member_AND_Room(Member member, Room room) {
    return MemberRoom.builder().member(member).room(room).role(RoomRole.OWNER).build();
  }

  public Room createUniqueRoom() {
    return Room.create("test-Room" + roomUniqueId++, "test-description" + roomUniqueId++);
  }

  public HttpHeaders create_Auth_HttpHeader_With_Member(Member member) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(
        jwtService.issueAccessToken(
            PrayTogetherPrincipal.builder().id(member.getId()).email(member.getEmail()).build()));
    return headers;
  }

  // MockMvc용 헬퍼 메서드들
  public String createBearerToken(Member member) {
    return "Bearer " + jwtService.issueAccessToken(
        PrayTogetherPrincipal.builder()
            .id(member.getId())
            .email(member.getEmail())
            .build());
  }

}
