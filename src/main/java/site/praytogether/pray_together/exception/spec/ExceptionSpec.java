package site.praytogether.pray_together.exception.spec;

import org.springframework.http.HttpStatus;

public interface ExceptionSpec {
    HttpStatus getStatue();
    String getCode();
    String getMessage(); // server log message
    String name();

}
