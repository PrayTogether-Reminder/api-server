package site.praytogether.pray_together.exception.spec;

import org.springframework.http.HttpStatus;

public interface ExceptionSpec {
  HttpStatus getStatus();

  String getCode();

  String getDebugMessage(); // server log message

  String name();
}
