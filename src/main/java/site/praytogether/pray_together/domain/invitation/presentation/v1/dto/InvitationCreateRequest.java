package site.praytogether.pray_together.domain.invitation.presentation.v1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvitationCreateRequest {

  @NotNull(message = "잘 못된 방을 선택하셨습니다.")
  @Positive(message = "잘 못된 방을 선택하셨습니다.")
  private final Long roomId;

  @NotNull(message = "이메일을 작성해 주세요.")
  @Email(message = "올바른 이메일 양식이 아닙니다.")
  private final String email;
}
