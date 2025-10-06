package site.praytogether.pray_together.domain.member.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class RoomMember {
  Long id;
  String name;
  PhoneNumber phoneNumber;

  public String getPhoneNumberSuffix() {
    if(phoneNumber == null) {
      return null;
    }
    return phoneNumber.getSuffix();
  }
}
