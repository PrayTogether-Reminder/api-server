package site.praytogether.pray_together.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberIdName {
  private final Long id;
  private final String name;
}
