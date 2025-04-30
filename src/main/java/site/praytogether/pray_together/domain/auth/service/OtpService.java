package site.praytogether.pray_together.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import site.praytogether.pray_together.domain.auth.cache.OtpCache;
import site.praytogether.pray_together.domain.auth.exception.OtpSendFailException;
import site.praytogether.pray_together.domain.auth.exception.OtpTemplateLoadFailException;

@Service
@RequiredArgsConstructor
public class OtpService {
  private final JavaMailSender mailSender;
  private final SecureRandom secureRandom;
  private final OtpCache otpCache;
  private final String EMAIL_OTP_TEMPLATE_PATH = "templates/email/otp.html";

  @Value("${spring.mail.username}")
  private String prayTogetherEmail;

  public void sendOtp(String email) {
    String otp = createOtp();
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
      setMessageContent(helper, email, otp);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new OtpSendFailException(email);
    }
    otpCache.put(email, otp); // Todo: set OTP TTL 3 minute
  }

  public boolean verifyOtp(String email, String otp) {
    String actualOtp = otpCache.get(email);

    if (actualOtp != null && actualOtp.equals(otp)) {
      otpCache.delete(email);
      return true;
    }
    return false;
  }

  private String createOtp() {
    int number = secureRandom.nextInt(1_000_000); // 0 ~ 999999 사이의 숫자
    return String.format("%06d", number);
  }

  private void setMessageContent(MimeMessageHelper helper, String email, String otp)
      throws MessagingException {
    helper.setFrom(prayTogetherEmail);
    helper.setTo(email);
    helper.setSubject("기도함께 이메일 인증번호");
    helper.setSentDate(new Date());

    String emailTemplate = loadEmailTemplate();
    String emailContent = String.format(emailTemplate, otp);

    helper.setText(emailContent, true);
  }

  private String loadEmailTemplate() {
    try {
      Resource resource = new ClassPathResource(EMAIL_OTP_TEMPLATE_PATH);
      byte[] templateBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
      return new String(templateBytes, "UTF-8");
    } catch (IOException e) {
      throw new OtpTemplateLoadFailException(EMAIL_OTP_TEMPLATE_PATH);
    }
  }
}
