package site.praytogether.pray_together.common.exception;

import java.util.StringJoiner;
import lombok.AccessLevel;
import lombok.Getter;
import site.praytogether.pray_together.common.exception.spec.ExceptionSpec;

@Getter
public abstract class BaseException extends RuntimeException{
    private final ExceptionSpec spec;
    @Getter(AccessLevel.NONE)
    private final ExceptionField fields;

    protected BaseException(ExceptionSpec spec, ExceptionField fields){
        this.spec = spec;
        this.fields = fields;
    }

    public String getLogMessage(){
        StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
        fields.get().forEach((key, value) -> joiner.add(key + "=" + value));
        return String.format(
                "[ERROR] %s : %s = %s %s",
                spec.getCode(), spec.name(), spec.getMessage(), joiner);
    }
}
