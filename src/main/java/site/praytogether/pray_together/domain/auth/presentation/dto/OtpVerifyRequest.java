package site.praytogether.pray_together.domain.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtpVerifyRequest {
  @NotBlank(message = "이메일을 입력해 주세요.")
  @Email(message = "유효한 이메일 형식이 아닙니다.")
  private final String email;

  @NotBlank(message = "OTP를 입력해 주세요.")
  private final String otp;
}
