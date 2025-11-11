package site.praytogether.pray_together.exception.spec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PrayerExceptionSpec implements ExceptionSpec {
  PRAYER_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "PRAYER-001", "기도 제목을 찾을 수 없습니다."),
  PRAYER_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRAYER-002", "기도 내용을 찾을 수 없습니다."),
  PRAYER_CONTENT_DUPLICATE_MEMBER(HttpStatus.BAD_REQUEST, "PRAYER-003", "이미 해당 기도 제목에 기도 내용을 작성한 회원입니다."),
  ;

  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
