package site.praytogether.pray_together.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.constant.CoreConstant.MemberConstant;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class PhoneNumber {

  @Column(name = "phone_number", nullable = true, length = MemberConstant.PHONE_NUMBER_MAX_LEN)
  private String value;

  private PhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isBlank()) {
      throw new IllegalArgumentException("전화번호를 입력해 주세요");
    }
    validate(phoneNumber);
    this.value = normalize(phoneNumber);
  }

  public static PhoneNumber of(String phoneNumber) {
    return new PhoneNumber(phoneNumber);
  }

  private static void validate(String phoneNumber) {
    // 하이픈 제거하고 검증
    String digitsOnly = phoneNumber.replaceAll("-", "");

    // 11자리 개인 휴대폰 번호 검증
    if (!digitsOnly.matches("^01[016789]\\d{8}$")) {
      throw new IllegalArgumentException(
          "올바른 휴대폰 번호 형식이 아닙니다. (010, 011, 016, 017, 018, 019만 가능)"
      );
    }
  }

  private static String normalize(String phoneNumber) {
    // 하이픈 제거하고 숫자만 추출
    String digitsOnly = phoneNumber.replaceAll("-", "");

    // 11자리 개인 휴대폰(010, 011, 016, 017, 018, 019): XXX-XXXX-XXXX 형식
    if (digitsOnly.length() == 11 && digitsOnly.matches("^01[016789].*")) {
      return digitsOnly.substring(0, 3) + "-"
           + digitsOnly.substring(3, 7) + "-"
           + digitsOnly.substring(7);
    }

    // 그 외는 그대로 반환 (여기 도달하면 안 됨, validate에서 걸림)
    return phoneNumber;
  }

  public String getSuffix() {
    if(value == null) {
      return null;
    }
    if (value.length() < 4) {
      throw new IllegalStateException("전화번호 형식이 올바르지 않습니다.");
    }
    return value.substring(value.length() - 4);
  }
}
