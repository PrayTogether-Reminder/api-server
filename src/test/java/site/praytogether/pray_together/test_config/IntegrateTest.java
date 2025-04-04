package site.praytogether.pray_together.test_config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import site.praytogether.pray_together.domain.member.repository.MemberRepository;
import site.praytogether.pray_together.domain.member_room.repository.MemberRoomRepository;
import site.praytogether.pray_together.domain.room.repository.RoomRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrateTest {
  @Autowired protected TestRestTemplate restTemplate;
  @Autowired protected RoomRepository roomRepository;
  @Autowired protected MemberRepository memberRepository;
  @Autowired protected MemberRoomRepository memberRoomRepository;
  @Autowired protected TestUtils testUtils;
  protected final String ROOMS_API_URL = "/api/v1/rooms";
}
