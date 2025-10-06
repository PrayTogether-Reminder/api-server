package site.praytogether.pray_together.domain.member.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SearchMemberDto {
  Long id;
  String name;
  String lastPhoneNumber;
}
