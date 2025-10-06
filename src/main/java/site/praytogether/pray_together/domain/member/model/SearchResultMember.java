package site.praytogether.pray_together.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SearchResultMember {
  Long id;
  String name;
  PhoneNumber phoneNumber;

  public String getLastPhoneNumber() {
    return phoneNumber.getLast();
  }
}
