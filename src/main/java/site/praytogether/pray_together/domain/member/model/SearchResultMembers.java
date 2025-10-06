package site.praytogether.pray_together.domain.member.model;

import java.util.List;
import lombok.Value;

@Value
public class SearchResultMembers {
  List<SearchResultMember> members;
}
