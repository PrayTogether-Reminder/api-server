package site.praytogether.pray_together.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionResponse {
    private final int status;
    private final String code;
    private final String message;

    public static ExceptionResponse of(int status, String code, String message){
        return new ExceptionResponse(status,code,message);
    }
}
