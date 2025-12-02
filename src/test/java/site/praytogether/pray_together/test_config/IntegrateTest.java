package site.praytogether.pray_together.test_config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;
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

  @PersistenceContext protected EntityManager entityManager;
  @Autowired protected TransactionTemplate transactionTemplate;

  protected final String API_VERSION_1 = "/api/v1";
  protected String API_VERSION_2 = "/api/v2";
  protected final String AUTH_API_URL = API_VERSION_1 + "/auth";
  protected final String ROOMS_API_URL = API_VERSION_1 + "/rooms";
  protected final String PRAYERS_API_URL = API_VERSION_1 + "/prayers";
  protected final String MEMBERS_API_URL = API_VERSION_1 + "/members";
  protected final String INVITATIONS_API_URL = API_VERSION_1 + "/invitations";
  protected final String FRIEND_API_URL = API_VERSION_1 + "/friends";

  @AfterEach
  void tearDown() {
    transactionTemplate.execute(status -> { // hibernate -> Insert, Update, Delete need Tx
      entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

      entityManager.getMetamodel().getEntities().forEach(entityType -> {
        String tableName = entityType.getName();
        entityManager.createQuery("DELETE FROM " + tableName).executeUpdate();
      });

      entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
      entityManager.flush();
      entityManager.clear();

      return null;
    });
  }
}
