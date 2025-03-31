package site.praytogether.pray_together.domain.member.expcetion;

import site.praytogether.pray_together.exception.spec.MemberExceptionSpec;

public class MemberNotFoundException extends MemberException {

  public MemberNotFoundException(Long memberId) {
    super(MemberExceptionSpec.MEMBER_NOT_FOUND, memberId);
  }

  @Override
  public String getClientMessage() {
    return "회원 정보를 찾을 수 없습니다.";
  }
}
