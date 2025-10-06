package site.praytogether.pray_together.domain.member.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SearchMemberResponse {
  List<SearchMemberDto> members;
}
