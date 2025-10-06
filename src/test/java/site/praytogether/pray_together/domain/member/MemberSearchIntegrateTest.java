package site.praytogether.pray_together.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.praytogether.pray_together.domain.member.dto.SearchMemberResponse;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

import java.util.ArrayList;
import java.util.List;

@DisplayName("회원 검색 통합 테스트")
public class MemberSearchIntegrateTest extends IntegrateTest {

  private String token;
  private Member member;
  private List<Member> testMembers;
  private String SEARCH_MEMBER_API_URL = MEMBERS_API_URL + "/search";

  @BeforeEach
  void setup() throws Exception {
    // 인증용 회원 생성
    member = testUtils.createUniqueMember();
    memberRepository.save(member);

    // 인증 토큰 생성
    token = testUtils.createBearerToken(member);

    // 검색용 테스트 회원 생성
    testMembers = new ArrayList<>();

    // "홍길동" 이름을 가진 회원들
    Member hong1 = testUtils.createUniqueMember_With_NameAndPhoneNumber("홍길동", "010-1234-5678");
    Member hong2 = testUtils.createUniqueMember_With_NameAndPhoneNumber("홍길동", "010-2345-6789");

    // "김철수" 이름을 가진 회원
    Member kim = testUtils.createUniqueMember_With_NameAndPhoneNumber("김철수", "010-3456-7890");

    // "이영희" 이름을 가진 회원
    Member lee = testUtils.createUniqueMember_With_NameAndPhoneNumber("이영희", "010-4567-8901");

    testMembers.add(hong1);
    testMembers.add(hong2);
    testMembers.add(kim);
    testMembers.add(lee);

    memberRepository.saveAll(testMembers);
  }

  @Test
  @DisplayName("회원 검색 API 요청 시 200 OK와 검색 결과 응답")
  void search_members_then_return_200_ok_with_results() throws Exception {
    // given
    String searchName = "홍길동";

    // when
    MvcResult result = mockMvc.perform(get(SEARCH_MEMBER_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .param("name", searchName))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    SearchMemberResponse response = objectMapper.readValue(responseBody, SearchMemberResponse.class);

    assertThat(response).as("회원 검색 API 응답 결과가 NULL 입니다.").isNotNull();
    assertThat(response.getMembers()).as("검색 결과 리스트가 NULL 입니다.").isNotNull();
    assertThat(response.getMembers().size()).as("검색 결과 수가 예상과 다릅니다.").isEqualTo(2);

    // 검색된 회원들의 이름 검증
    response.getMembers().forEach(memberDto -> {
      assertThat(memberDto.getName()).as("검색된 회원 이름이 검색어를 포함하지 않습니다.").contains(searchName);
    });
  }

  @Test
  @DisplayName("존재하지 않는 회원 검색 시 200 OK와 빈 리스트 응답")
  void search_non_existing_member_then_return_200_ok_with_empty_list() throws Exception {
    // given
    String searchName = "존재하지않는이름";

    // when
    MvcResult result = mockMvc.perform(get(SEARCH_MEMBER_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .param("name", searchName))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    SearchMemberResponse response = objectMapper.readValue(responseBody, SearchMemberResponse.class);

    assertThat(response).as("회원 검색 API 응답 결과가 NULL 입니다.").isNotNull();
    assertThat(response.getMembers()).as("검색 결과 리스트가 NULL 입니다.").isNotNull();
    assertThat(response.getMembers().size()).as("존재하지 않는 회원 검색 시 빈 리스트가 반환되어야 합니다.").isEqualTo(0);
  }

  @Test
  @DisplayName("부분 검색으로 여러 회원 조회 시 200 OK와 검색 결과 응답")
  void search_members_with_partial_name_then_return_200_ok_with_results() throws Exception {
    // given
    String searchName = "길";

    // when
    MvcResult result = mockMvc.perform(get(SEARCH_MEMBER_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .param("name", searchName))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    SearchMemberResponse response = objectMapper.readValue(responseBody, SearchMemberResponse.class);

    assertThat(response).as("회원 검색 API 응답 결과가 NULL 입니다.").isNotNull();
    assertThat(response.getMembers()).as("검색 결과 리스트가 NULL 입니다.").isNotNull();
    assertThat(response.getMembers().size()).as("부분 검색 결과 수가 예상과 다릅니다.").isGreaterThanOrEqualTo(1);

    // 검색된 회원들의 이름이 검색어를 포함하는지 검증
    response.getMembers().forEach(memberDto -> {
      assertThat(memberDto.getName()).as("검색된 회원 이름이 검색어를 포함하지 않습니다.").contains(searchName);
    });
  }

  @Test
  @DisplayName("검색 결과에 전화번호 뒷자리가 포함되는지 검증")
  void search_members_then_response_contains_last_phone_number() throws Exception {
    // given
    String searchName = "홍길동";

    // when
    MvcResult result = mockMvc.perform(get(SEARCH_MEMBER_API_URL)
            .header(HttpHeaders.AUTHORIZATION, token)
            .param("name", searchName))
        .andExpect(status().isOk())
        .andReturn();

    // then
    String responseBody = result.getResponse().getContentAsString();
    SearchMemberResponse response = objectMapper.readValue(responseBody, SearchMemberResponse.class);

    assertThat(response.getMembers()).as("검색 결과 리스트가 NULL 입니다.").isNotNull();
    assertThat(response.getMembers().size()).as("검색 결과 수가 예상과 다릅니다.").isEqualTo(2);

    // 검색된 회원들의 전화번호 뒷자리 검증
    response.getMembers().forEach(memberDto -> {
      assertThat(memberDto.getPhoneNumberSuffix()).as("전화번호 뒷자리가 NULL 입니다.").isNotNull();
      assertThat(memberDto.getPhoneNumberSuffix()).as("전화번호 뒷자리가 비어있습니다.").isNotBlank();
      assertThat(memberDto.getPhoneNumberSuffix().length()).as("전화번호 뒷자리는 정확히 4자리여야 합니다.").isEqualTo(4);
      assertThat(memberDto.getPhoneNumberSuffix()).as("전화번호 뒷자리는 숫자로만 구성되어야 합니다.").matches("\\d{4}");
    });

    // 실제 생성한 회원의 전화번호 뒷자리와 일치하는지 검증
    assertThat(response.getMembers())
        .as("검색 결과에 '5678' 뒷자리를 가진 회원이 포함되어야 합니다.")
        .anyMatch(memberDto -> memberDto.getPhoneNumberSuffix().equals("5678"));

    assertThat(response.getMembers())
        .as("검색 결과에 '6789' 뒷자리를 가진 회원이 포함되어야 합니다.")
        .anyMatch(memberDto -> memberDto.getPhoneNumberSuffix().equals("6789"));
  }
}
