package site.praytogether.pray_together.domain.invitation.presentation.v2.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvitationCreateRequestV2 {

  @NotNull(message = "잘 못된 방을 선택하셨습니다.")
  @Positive(message = "잘 못된 방을 선택하셨습니다.")
  private final Long roomId;

  @Positive(message = "친구 선택이 잘 못 되었습니다.")
  private final Long friendId;
}
