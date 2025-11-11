package site.praytogether.pray_together.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UpdateProfileRequest {

  @Size(min = 1, max = 10, message = "이름은 1자 이상 10자 이하로 입력해 주세요.")
  String name;

  @Pattern(
      regexp = "^01[016789]-?\\d{4}-?\\d{4}$",
      message = "올바른 휴대폰 번호 형식이 아닙니다."
  )
  String phoneNumber;
}
