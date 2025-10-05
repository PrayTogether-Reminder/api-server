package site.praytogether.pray_together.domain.auth.model;

import lombok.Builder;
import lombok.Value;
import site.praytogether.pray_together.domain.auth.dto.SignupRequest;

@Value
@Builder
public class SignupCommand {
  String name;
  String email;
  String password;
  String phoneNumber;

  public static SignupCommand from(SignupRequest request) {
    String normalizedPhone = normalizePhoneNumber(request.getPhoneNumber());
    return SignupCommand.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(request.getPassword())
        .phoneNumber(normalizedPhone)
        .build();
  }

  private static String normalizePhoneNumber(String phoneNumber) {
    if (phoneNumber == null) {
      return null;
    }

    // 하이픈 제거하고 숫자만 추출
    String digitsOnly = phoneNumber.replaceAll("-", "");

    // 11자리 개인 휴대폰(010, 011, 016, 017, 018, 019): XXX-XXXX-XXXX 형식
    if (digitsOnly.length() == 11 && digitsOnly.matches("^01[016789].*")) {
      return digitsOnly.substring(0, 3) + "-"
           + digitsOnly.substring(3, 7) + "-"
           + digitsOnly.substring(7);
    }

    // 그 외는 그대로 반환
    return phoneNumber;
  }
}
