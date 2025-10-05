package site.praytogether.pray_together.domain.auth.model;

import lombok.Builder;
import lombok.Value;
import site.praytogether.pray_together.domain.auth.dto.SignupRequest;
import site.praytogether.pray_together.domain.member.model.PhoneNumber;

@Value
@Builder
public class SignupCommand {
  String name;
  String email;
  String password;
  PhoneNumber phoneNumber;

  public static SignupCommand from(SignupRequest request) {
    PhoneNumber phoneNumber = PhoneNumber.of(request.getPhoneNumber());
    return SignupCommand.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(request.getPassword())
        .phoneNumber(phoneNumber)
        .build();
  }
}
