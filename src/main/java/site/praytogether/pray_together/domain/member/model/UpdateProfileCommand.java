package site.praytogether.pray_together.domain.member.model;

import lombok.Builder;
import lombok.Value;
import site.praytogether.pray_together.domain.member.dto.UpdateProfileRequest;

@Value
@Builder
public class UpdateProfileCommand {
  String name;
  PhoneNumber phoneNumber;

  public static UpdateProfileCommand from(UpdateProfileRequest request) {
    PhoneNumber phoneNumber = null;
    if(request.getPhoneNumber() != null) {
      phoneNumber = PhoneNumber.of(request.getPhoneNumber());
    }

    return UpdateProfileCommand.builder()
        .name(request.getName())
        .phoneNumber(phoneNumber)
        .build();
  }
}
