package site.praytogether.pray_together.test_config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import org.mockito.Mockito;
import java.util.concurrent.Executor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;

@TestConfiguration
public class IntegrateTestConfig {

  @Bean
  @Primary
  public FirebaseApp mockFirebaseApp() {
    return Mockito.mock(FirebaseApp.class);
  }

  @Bean
  @Primary
  public FirebaseMessaging mockFirebaseMessaging() {
    return Mockito.mock(FirebaseMessaging.class);
  }

  @Bean(name = "notificationExecutor")
  @Primary
  public Executor notificationExecutor() {
    return new SyncTaskExecutor();
  }
}
