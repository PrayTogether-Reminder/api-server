package site.praytogether.pray_together.domain.member.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchQueryMember {
  String name;

  public static SearchQueryMember from(String searchName) {
    if(searchName == null || searchName.isBlank()) {
      throw new IllegalArgumentException("검색할 회원 이름을 입력해 주세요.");
    }
    return new SearchQueryMember(searchName);
  }
}
