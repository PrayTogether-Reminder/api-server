package site.praytogether.pray_together.domain.member.application.mapper;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import site.praytogether.pray_together.domain.member.model.SearchResultMember;
import site.praytogether.pray_together.domain.member.model.SearchResultMembers;
import site.praytogether.pray_together.domain.member.dto.SearchMemberDto;
import site.praytogether.pray_together.domain.member.dto.SearchMemberResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchMemberMapper {

  public static SearchMemberDto toDto(SearchResultMember member) {
    return SearchMemberDto.builder()
        .id(member.getId())
        .name(member.getName())
        .phoneNumberSuffix(member.getPhoneNumberSuffix())
        .build();
  }

  public static SearchMemberResponse toResponse(SearchResultMembers members) {
    List<SearchMemberDto> dtos = members.getMembers()
        .stream()
        .map(SearchMemberMapper::toDto)
        .toList();
    return new SearchMemberResponse(dtos);
  }
}
