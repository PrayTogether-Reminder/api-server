package site.praytogether.pray_together.domain.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
public class ChangePasswordRequest {
  @NotBlank(message = "비밀번호를 입력해 주세요")
  String newPassword;
}
