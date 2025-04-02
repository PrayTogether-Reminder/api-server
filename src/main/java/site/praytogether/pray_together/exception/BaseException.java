package site.praytogether.pray_together.exception;

import java.util.StringJoiner;
import lombok.AccessLevel;
import lombok.Getter;
import site.praytogether.pray_together.exception.spec.ExceptionSpec;

@Getter
public abstract class BaseException extends RuntimeException {
  private final ExceptionSpec exceptionSpec;

  @Getter(AccessLevel.NONE)
  private final ExceptionField exceptionField;

  protected BaseException(ExceptionSpec spec, ExceptionField fields) {
    this.exceptionSpec = spec;
    this.exceptionField = fields;
  }

  public String getLogMessage() {
    StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
    exceptionField.get().forEach((key, value) -> joiner.add(key + "=" + value));
    return String.format(
        "[ERROR] %s : %s = %s %s",
        exceptionSpec.getCode(), exceptionSpec.name(), exceptionSpec.getDebugMessage(), joiner);
  }

  public abstract String getClientMessage();
}
