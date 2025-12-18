package site.praytogether.pray_together.domain.prayer.domain.exception;

import site.praytogether.pray_together.exception.BaseException;
import site.praytogether.pray_together.exception.ExceptionField;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

public abstract class PrayerException extends BaseException {

  protected PrayerException(ExceptionSpec spec, ExceptionField fields) {
    super(spec, fields);
  }
}
