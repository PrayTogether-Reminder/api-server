package site.praytogether.pray_together.domain.prayer.exception;

import static site.praytogether.pray_together.exception.spec.PrayerExceptionSpec.PRAYER_CONTENT_DUPLICATE_MEMBER;

import site.praytogether.pray_together.exception.ExceptionField;

public class PrayerContentDuplicateMemberException extends PrayerException {

  public PrayerContentDuplicateMemberException(Long titleId, String memberName) {
    this(ExceptionField.builder()
        .add("titleId", titleId)
        .add("memberName", memberName)
        .build());
  }

  @Override
  public String getClientMessage() {
    return "이미 해당 기도 제목에 기도 내용을 작성하셨습니다.";
  }

  protected PrayerContentDuplicateMemberException(ExceptionField fields) {
    super(PRAYER_CONTENT_DUPLICATE_MEMBER, fields);
  }
}