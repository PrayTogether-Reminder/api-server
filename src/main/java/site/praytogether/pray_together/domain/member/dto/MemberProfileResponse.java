package site.praytogether.pray_together.domain.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.member.model.MemberProfile;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileResponse {
  private Long id;
  private String name;
  private String email;
  private String phoneNumber;

  public static MemberProfileResponse from(MemberProfile profile) {
    String phoneNumber = null;
    if(profile.getPhoneNumber() != null) {
      phoneNumber = profile.getPhoneNumber().getValue();
    }
    return MemberProfileResponse.builder()
        .id(profile.getId())
        .email(profile.getEmail())
        .name(profile.getName())
        .phoneNumber(phoneNumber)
        .build();
  }
}
