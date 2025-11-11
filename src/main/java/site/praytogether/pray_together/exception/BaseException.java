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

  public String
      getLogMessage() { // todo: Client 메시지는 Global exception 혹은 개별 커스텀 Exception에서 직접 생성하여 처리하기,
                        // Log는 spec 처리하기
    StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
    exceptionField.get().forEach((key, value) -> joiner.add(key + "=" + value));
    return String.format(
        "[ERROR] %s : %s = %s %s",
        exceptionSpec.getCode(), exceptionSpec.name(), exceptionSpec.getDebugMessage(), joiner);
  }

  public abstract String getClientMessage();
}
