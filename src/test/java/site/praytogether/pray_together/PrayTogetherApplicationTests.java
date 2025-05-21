package site.praytogether.pray_together;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import site.praytogether.pray_together.test_config.IntegrateTestConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(IntegrateTestConfig.class)
class PrayTogetherApplicationTests {

  @Test
  void contextLoads() {}
}
