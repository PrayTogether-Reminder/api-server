package site.praytogether.pray_together.domain.member.expcetion;

import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.MemberExceptionSpec;

public class MemberAlreadyExistException extends MemberException {

  public MemberAlreadyExistException(String email) {
    this(ExceptionField.builder().add("email", email).build());
  }

  protected MemberAlreadyExistException(ExceptionField field) {
    super(MemberExceptionSpec.MEMBER_NOT_FOUND, field);
  }

  @Override
  public String getClientMessage() {
    return "이미 등록된 회원 정보가 있습니다.";
  }
}
