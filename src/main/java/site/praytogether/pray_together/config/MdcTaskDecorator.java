package site.praytogether.pray_together.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

public class MdcTaskDecorator implements TaskDecorator {

  @Override
  @NonNull
  public Runnable decorate(@NonNull Runnable runnable) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
      if (contextMap != null) {
        MDC.setContextMap(contextMap);
      }
      try {
        runnable.run();
      } finally {
        MDC.clear();
      }
    };
  }
}
