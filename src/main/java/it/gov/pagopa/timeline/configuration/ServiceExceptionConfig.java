package it.gov.pagopa.timeline.configuration;

import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.timeline.exception.custom.RefundsNotFoundException;
import it.gov.pagopa.timeline.exception.custom.TimelineDetailNotFoundException;
import it.gov.pagopa.timeline.exception.custom.UserNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceExceptionConfig {
    @Bean
    public Map<Class<? extends ServiceException>, HttpStatus> serviceExceptionMapper() {
        Map<Class<? extends ServiceException>, HttpStatus> exceptionMap = new HashMap<>();

        // NotFound
        exceptionMap.put(RefundsNotFoundException.class, HttpStatus.NOT_FOUND);
        exceptionMap.put(TimelineDetailNotFoundException.class, HttpStatus.NOT_FOUND);
        exceptionMap.put(UserNotFoundException.class, HttpStatus.NOT_FOUND);

        return exceptionMap;
    }
}
