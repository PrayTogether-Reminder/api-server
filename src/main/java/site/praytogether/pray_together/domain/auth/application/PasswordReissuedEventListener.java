package site.praytogether.pray_together.domain.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import site.praytogether.pray_together.domain.auth.domain.EmailSender;
import site.praytogether.pray_together.domain.auth.domain.event.PasswordReissuedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordReissuedEventListener {
  private final EmailSender emailSender;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Async("emailExecutor")
  public void handlePasswordReissued(PasswordReissuedEvent event) {
    try {
      log.info("[Email] 비밀번호 재발급 이메일 발송 시작");
      emailSender.sendNewPassword(event.getEmail(), event.getNewPassword());
      log.info("[Email] 비밀번호 재발급 이메일 발송 완료");
    } catch (Exception e) {
      log.error("[Email]비밀번호 재발급 이메일 발송 실패: error={}", e.getMessage(), e);
    }
  }
}
