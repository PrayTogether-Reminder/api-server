package site.praytogether.pray_together.test_config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import site.praytogether.pray_together.domain.auth.repository.RefreshTokenRepository;
import site.praytogether.pray_together.domain.friend.domain.friend_invitation.FriendInvitationRepository;
import site.praytogether.pray_together.domain.friend.domain.friendship.FriendshipRepository;
import site.praytogether.pray_together.domain.invitation.domain.repository.InvitationRepository;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;
import site.praytogether.pray_together.domain.member_room.repository.MemberRoomRepository;
import site.praytogether.pray_together.domain.notification.repository.PrayerCompletionNotificationRepository;
import site.praytogether.pray_together.domain.prayer.respository.PrayerCompletionRepository;
import site.praytogether.pray_together.domain.prayer.respository.PrayerContentRepository;
import site.praytogether.pray_together.domain.prayer.respository.PrayerTitleRepository;
import site.praytogether.pray_together.domain.room.repository.RoomRepository;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(value = IntegrateTestConfig.class)
public class IntegrateTest {
  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected RoomRepository roomRepository;
  @Autowired protected MemberRepository memberRepository;
  @Autowired protected MemberRoomRepository memberRoomRepository;
  @Autowired protected PrayerTitleRepository prayerTitleRepository;
  @Autowired protected PrayerContentRepository prayerContentRepository;
  @Autowired protected InvitationRepository invitationRepository;
  @Autowired protected PrayerCompletionRepository prayerCompletionRepository;
  @Autowired protected FriendInvitationRepository friendInvitationRepository;
  @Autowired protected FriendshipRepository friendshipRepository;
  @Autowired protected RefreshTokenRepository refreshTokenRepository;

  @Autowired
  protected PrayerCompletionNotificationRepository prayerCompletionNotificationRepository;

  @Autowired protected TestUtils testUtils;

  protected final String API_VERSION_1 = "/api/v1";
  protected String API_VERSION_2 = "/api/v2";
  protected final String AUTH_API_URL = API_VERSION_1 + "/auth";
  protected final String ROOMS_API_URL = API_VERSION_1 + "/rooms";
  protected final String PRAYERS_API_URL = API_VERSION_1 + "/prayers";
  protected final String MEMBERS_API_URL = API_VERSION_1 + "/members";
  protected final String INVITATIONS_API_URL = API_VERSION_1 + "/invitations";
  protected final String FRIEND_API_URL = API_VERSION_1 + "/friends";
}
