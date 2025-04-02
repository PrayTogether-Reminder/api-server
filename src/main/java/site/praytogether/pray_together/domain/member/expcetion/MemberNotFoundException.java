package site.praytogether.pray_together.domain.member.expcetion;

import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.MemberExceptionSpec;

public class MemberNotFoundException extends MemberException {

  public MemberNotFoundException(Long memberId) {
    this(ExceptionField.builder().add("memberId", memberId).build());
  }

  protected MemberNotFoundException(ExceptionField field) {
    super(MemberExceptionSpec.MEMBER_NOT_FOUND, field);
  }

  @Override
  public String getClientMessage() {
    return "회원 정보를 찾을 수 없습니다.";
  }
}
