package site.praytogether.pray_together.domain.prayer.exception;

import static site.praytogether.pray_together.exception.spec.PrayerExceptionSpec.PRAYER_TITLE_NOT_FOUND;

import site.praytogether.pray_together.exception.ExceptionField;

public class PrayerTitleNotFoundException extends PrayerException {

  public PrayerTitleNotFoundException(Long titleId) {
    this(ExceptionField.builder().add("prayer-titleId", titleId).build());
  }

  @Override
  public String getClientMessage() {
    return "기도 제목을 찾을 수 없습니다.";
  }

  protected PrayerTitleNotFoundException(ExceptionField fields) {
    super(PRAYER_TITLE_NOT_FOUND, fields);
  }
}
