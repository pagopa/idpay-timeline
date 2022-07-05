package it.gov.pagopa.timeline.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TimelineException extends RuntimeException {

  private final int code;

  private final String message;

}
