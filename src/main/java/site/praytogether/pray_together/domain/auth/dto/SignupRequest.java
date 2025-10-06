package site.praytogether.pray_together.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequest {

  @NotBlank(message = "이름을 입력해 주세요.")
  @Size(min = 1, max = 10, message = "이름은 1자 이상 10자 이하로 입력해 주세요.")
  private final String name;

  @NotBlank(message = "이메일을 입력해 주세요.")
  @Email(message = "유효한 이메일 형식이 아닙니다.")
  private final String email;

  @NotBlank(message = "전화번호를 입력해 주세요")
  @Pattern(
      regexp = "^01[016789]-?\\d{4}-?\\d{4}$",
      message = "올바른 휴대폰 번호 형식이 아닙니다."
  )
  private final String phoneNumber;

  @NotBlank(message = "비밀번호를 입력해 주세요.")
  @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해 주세요.")
  private final String password;
}
