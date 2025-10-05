package site.praytogether.pray_together.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import site.praytogether.pray_together.domain.auth.dto.SignupRequest;
import site.praytogether.pray_together.domain.member.model.Member;
import site.praytogether.pray_together.test_config.IntegrateTest;

@DisplayName("회원가입 통합 테스트")
public class SignupCreateIntegrateTest extends IntegrateTest {

  private final String SIGNUP_URL = AUTH_API_URL + "/signup";

  @DisplayName("정상적인 입력으로 회원가입 시 201 Created 응답 및 전화번호 정규화 저장")
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("provideValidSignupArguments")
  void signup_with_valid_input_then_return_201_and_normalize_phone(
      String testName, String name, String email, String phoneInput, String expectedPhone, String password)
      throws Exception {

    // given
    SignupRequest request = new SignupRequest(name, email, phoneInput, password);

    // when & then
    mockMvc.perform(post(SIGNUP_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").exists());

    // DB 검증
    List<Member> allMembers = memberRepository.findAll();
    assertThat(allMembers).hasSize(1);

    Member savedMember = allMembers.get(0);
    assertThat(savedMember.getName()).isEqualTo(name);
    assertThat(savedMember.getEmail()).isEqualTo(email);
    assertThat(savedMember.getPhoneNumber()).isEqualTo(expectedPhone);
  }

  private static Stream<Arguments> provideValidSignupArguments() {
    return Stream.of(
        // 010 번호
        Arguments.of("010 하이픈 없음", "홍길동", "test1@example.com", "01012345678", "010-1234-5678", "password123"),
        Arguments.of("010 하이픈 있음", "김철수", "test2@example.com", "010-1234-5678", "010-1234-5678", "password123"),

        // 011 번호
        Arguments.of("011 하이픈 없음", "박민수", "test4@example.com", "01112345678", "011-1234-5678", "password123"),
        Arguments.of("011 하이픈 있음", "최지훈", "test5@example.com", "011-1234-5678", "011-1234-5678", "password123"),

        // 016 번호
        Arguments.of("016 하이픈 없음", "정수진", "test6@example.com", "01612345678", "016-1234-5678", "password123"),
        Arguments.of("016 하이픈 있음", "강서연", "test7@example.com", "016-1234-5678", "016-1234-5678", "password123"),

        // 017 번호
        Arguments.of("017 하이픈 없음", "윤하늘", "test8@example.com", "01712345678", "017-1234-5678", "password123"),

        // 018 번호
        Arguments.of("018 하이픈 없음", "임도현", "test9@example.com", "01812345678", "018-1234-5678", "password123"),

        // 019 번호
        Arguments.of("019 하이픈 없음", "한지우", "test10@example.com", "01912345678", "019-1234-5678", "password123")
    );
  }

  @DisplayName("회원가입 요청 유효성 검사 실패 시 400 Bad Request 응답")
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("provideInvalidSignupArguments")
  void signup_with_invalid_input_then_return_400_bad_request(
      String testName, String name, String email, String phoneNumber, String password) throws Exception {

    // given
    SignupRequest request = new SignupRequest(name, email, phoneNumber, password);

    // when & then
    mockMvc.perform(post(SIGNUP_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    // DB 검증 - 회원이 생성되지 않았는지 확인
    assertThat(memberRepository.findAll()).isEmpty();
  }

  private static Stream<Arguments> provideInvalidSignupArguments() {
    String validName = "홍길동";
    String validEmail = "test@example.com";
    String validPhone = "01012345678";
    String validPassword = "password123";

    return Stream.of(
        // --- 이름 유효성 검사 ---
        Arguments.of("이름이 null일 때", null, validEmail, validPhone, validPassword),
        Arguments.of("이름이 빈 문자열일 때", "", validEmail, validPhone, validPassword),
        Arguments.of("이름이 공백만일 때", "   ", validEmail, validPhone, validPassword),
        Arguments.of("이름이 10자 초과일 때", "a".repeat(11), validEmail, validPhone, validPassword),

        // --- 이메일 유효성 검사 ---
        Arguments.of("이메일이 null일 때", validName, null, validPhone, validPassword),
        Arguments.of("이메일이 빈 문자열일 때", validName, "", validPhone, validPassword),
        Arguments.of("이메일이 공백만일 때", validName, "   ", validPhone, validPassword),
        Arguments.of("이메일 형식이 잘못되었을 때 (@없음)", validName, "invalidemail.com", validPhone, validPassword),
        Arguments.of("이메일 형식이 잘못되었을 때 (도메인 없음)", validName, "test@", validPhone, validPassword),

        // --- 전화번호 유효성 검사 ---
        Arguments.of("전화번호가 null일 때", validName, validEmail, null, validPassword),
        Arguments.of("전화번호가 빈 문자열일 때", validName, validEmail, "", validPassword),
        Arguments.of("전화번호가 공백만일 때", validName, validEmail, "   ", validPassword),
        Arguments.of("전화번호가 지역번호일 때 (02)", validName, validEmail, "0212345678", validPassword),
        Arguments.of("전화번호가 너무 짧을 때", validName, validEmail, "01012", validPassword),
        Arguments.of("전화번호가 015로 시작할 때 (개인 휴대폰 아님)", validName, validEmail, "01512345678", validPassword),
        Arguments.of("전화번호에 문자가 포함될 때", validName, validEmail, "010-abcd-5678", validPassword),

        // --- 비밀번호 유효성 검사 ---
        Arguments.of("비밀번호가 null일 때", validName, validEmail, validPhone, null),
        Arguments.of("비밀번호가 빈 문자열일 때", validName, validEmail, validPhone, ""),
        Arguments.of("비밀번호가 공백만일 때", validName, validEmail, validPhone, "     "),
        Arguments.of("비밀번호가 6자 미만일 때", validName, validEmail, validPhone, "pass"),
        Arguments.of("비밀번호가 15자 초과일 때", validName, validEmail, validPhone, "a".repeat(16))
    );
  }
}
