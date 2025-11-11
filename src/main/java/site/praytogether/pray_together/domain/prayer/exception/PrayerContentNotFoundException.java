package site.praytogether.pray_together.domain.prayer.exception;

import static site.praytogether.pray_together.exception.spec.PrayerExceptionSpec.PRAYER_CONTENT_NOT_FOUND;

import site.praytogether.pray_together.exception.ExceptionField;

public class PrayerContentNotFoundException extends PrayerException {

  public PrayerContentNotFoundException(Long contentId) {
    this(ExceptionField.builder().add("prayer-contentId", contentId).build());
  }

  public PrayerContentNotFoundException(Long contentId, Long titleId) {
    this(ExceptionField.builder()
        .add("prayer-contentId", contentId)
        .add("prayer-titleId", titleId)
        .build());
  }

  @Override
  public String getClientMessage() {
    return "기도 내용을 찾을 수 없습니다.";
  }

  protected PrayerContentNotFoundException(ExceptionField fields) {
    super(PRAYER_CONTENT_NOT_FOUND, fields);
  }
}