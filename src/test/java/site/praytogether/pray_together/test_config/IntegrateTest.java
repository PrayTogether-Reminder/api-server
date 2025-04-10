package site.praytogether.pray_together.test_config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import site.praytogether.pray_together.domain.invitation.repository.InvitationRepository;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;
import site.praytogether.pray_together.domain.member_room.repository.MemberRoomRepository;
import site.praytogether.pray_together.domain.prayer.respository.PrayerContentRepository;
import site.praytogether.pray_together.domain.prayer.respository.PrayerTitleRepository;
import site.praytogether.pray_together.domain.room.repository.RoomRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrateTest {
  @Autowired protected TestRestTemplate restTemplate;
  @Autowired protected RoomRepository roomRepository;
  @Autowired protected MemberRepository memberRepository;
  @Autowired protected MemberRoomRepository memberRoomRepository;
  @Autowired protected PrayerTitleRepository prayerTitleRepository;
  @Autowired protected PrayerContentRepository prayerContentRepository;
  @Autowired protected InvitationRepository invitationRepository;
  @Autowired protected TestUtils testUtils;

  private final String API_VERSION = "/api/v1";
  protected final String ROOMS_API_URL = API_VERSION + "/rooms";
  protected final String PRAYERS_API_URL = API_VERSION + "/prayers";
  protected final String MEMBERS_API_URL = API_VERSION + "/members";
  protected final String INVITATIONS_API_URL = API_VERSION + "/invitations";

  protected void cleanRepository() {
    // delete order is very important
    invitationRepository.deleteAll();
    prayerContentRepository.deleteAll();
    prayerTitleRepository.deleteAll();
    memberRoomRepository.deleteAll();
    roomRepository.deleteAll();
    memberRepository.deleteAll();
  }
}
