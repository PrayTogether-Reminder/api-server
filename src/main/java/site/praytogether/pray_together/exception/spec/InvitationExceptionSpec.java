package site.praytogether.pray_together.exception.spec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InvitationExceptionSpec implements ExceptionSpec {
  INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITATION-001", "방 초대장을 찾을 수 없습니다."),
  ALREADY_RESPONDED_INVITATION(
      HttpStatus.BAD_REQUEST, "INVITATION-002", "이미 응답 설정(ACCEPT/REJECT)이 된 방 초대장 입니다."),
  ;

  private final HttpStatus status;
  private final String code;
  private final String debugMessage;
}
