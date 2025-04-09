package site.praytogether.pray_together.exception.spec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PrayerExceptionSpec implements ExceptionSpec {
  PRAYER_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "PRAYER-001", "기도 제목을 찾을 수 없습니다."),
  ;

  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
