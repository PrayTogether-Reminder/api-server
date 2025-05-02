package site.praytogether.pray_together.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignupRequest {

  @NotBlank(message = "이름을 입력해 주세요.")
  @Size(min = 1, max = 10, message = "이름은 1자 이상 10자 이하로 입력해 주세요.")
  private final String name;

  @NotBlank(message = "이메일을 입력해 주세요.")
  @Email(message = "유효한 이메일 형식이 아닙니다.")
  private final String email;

  @NotBlank(message = "비밀번호를 입력해 주세요.")
  @Size(min = 6, max = 15, message = "비밀번호는 6자 이상 15자 이하로 입력해 주세요.")
  //  @Pattern(regexp = "^.*[!@#$%^&*(),.?\":{}|<>].*$", message = "비밀번호는 최소 1개 이상의 특수 문자를 포함해야
  // 합니다.")
  private final String password;
}
