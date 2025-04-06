package site.praytogether.pray_together.domain.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RoomCreateRequest {
  @NotBlank(message = "방 이름을 작성해 주세요.")
  @Size(min = 1, max = 50, message = "방 이름은 50자 이하로 작성해 주세요.")
  private final String name;

  @NotBlank(message = "방 설명을 작성해 주세요.")
  @Size(min = 1, max = 200, message = "방 이름은 200자 이하로 작성해 주세요.")
  private final String description;
}
