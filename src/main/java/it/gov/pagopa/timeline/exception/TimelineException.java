package it.gov.pagopa.timeline.exception;

import it.gov.pagopa.common.web.exception.ClientExceptionWithBody;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@SuppressWarnings("squid:S110")
public class TimelineException extends ClientExceptionWithBody {
  public TimelineException(int code, String message) {
    super(HttpStatus.valueOf(code), code, message);
  }
}
