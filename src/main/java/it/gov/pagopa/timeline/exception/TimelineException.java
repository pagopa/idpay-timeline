package it.gov.pagopa.timeline.exception;

import it.gov.pagopa.common.web.exception.ClientExceptionNoBody;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@SuppressWarnings("squid:S110")
public class TimelineException extends ClientExceptionNoBody {
  public TimelineException(HttpStatus httpStatus, String message) {
    super(httpStatus, message);
  }
}
