package site.praytogether.pray_together.domain.invitation.presentation.v2.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvitationCreateRequestV2 {

  @NotNull(message = "잘 못된 방을 선택하셨습니다.")
  @Positive(message = "잘 못된 방을 선택하셨습니다.")
  private final Long roomId;

  @Size(min = 1,message = "친구를 선택해 주세요.")
  private final List<@Positive(message = "친구 선택이 잘 못 되었습니다.") Long> friendIds;
}
