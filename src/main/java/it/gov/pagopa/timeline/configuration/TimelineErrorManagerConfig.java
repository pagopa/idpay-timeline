package it.gov.pagopa.timeline.configuration;

import it.gov.pagopa.common.web.dto.ErrorDTO;
import it.gov.pagopa.timeline.constants.TimelineConstants.ExceptionCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimelineErrorManagerConfig {

  @Bean
  ErrorDTO defaultErrorDTO() {
    return new ErrorDTO(
        ExceptionCode.TIMELINE_GENERIC_ERROR,
        "A generic error occurred"
    );
  }

  @Bean
  ErrorDTO tooManyRequestsErrorDTO() {
    return new ErrorDTO(ExceptionCode.TIMELINE_TOO_MANY_REQUESTS, "Too Many Requests");
  }

  @Bean
  ErrorDTO templateValidationErrorDTO(){
    return new ErrorDTO(ExceptionCode.TIMELINE_INVALID_REQUEST, null);
  }
}
