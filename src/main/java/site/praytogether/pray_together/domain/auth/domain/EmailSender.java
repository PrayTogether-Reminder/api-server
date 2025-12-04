package site.praytogether.pray_together.domain.auth.domain;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import site.praytogether.pray_together.domain.auth.domain.exception.OtpSendFailException;
import site.praytogether.pray_together.domain.auth.domain.exception.OtpTemplateLoadFailException;

@RequiredArgsConstructor
@Component
public class EmailSender {
  private final JavaMailSender mailSender;
  private final String EMAIL_PASSWORD_TEMPLATE_PATH = "templates/email/reissue-password.html";

  @Value("${spring.mail.username}")
  private String prayTogetherEmail;

  public void sendNewPassword(String email, String newPassword) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
      setMessageContent(helper, email, newPassword);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new OtpSendFailException(email);
    }
  }

  private void setMessageContent(MimeMessageHelper helper, String email, String newPassword)
      throws MessagingException {
    helper.setFrom(prayTogetherEmail);
    helper.setTo(email);
    helper.setSubject("기도함께 임시 비밀번호 안내");
    helper.setSentDate(new Date());

    String emailTemplate = loadEmailTemplate();
    String emailContent = String.format(emailTemplate, newPassword);

    helper.setText(emailContent, true);
  }

  private String loadEmailTemplate() {
    try {
      Resource resource = new ClassPathResource(EMAIL_PASSWORD_TEMPLATE_PATH);
      byte[] templateBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
      return new String(templateBytes, "UTF-8");
    } catch (IOException e) {
      throw new OtpTemplateLoadFailException(EMAIL_PASSWORD_TEMPLATE_PATH);
    }
  }
}
