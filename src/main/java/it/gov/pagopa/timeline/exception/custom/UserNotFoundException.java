package it.gov.pagopa.timeline.exception.custom;

import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.common.web.exception.ServiceExceptionResponse;
import it.gov.pagopa.timeline.constants.TimelineConstants;

public class UserNotFoundException extends ServiceException {


    public UserNotFoundException(String message) {
        this(TimelineConstants.ExceptionCode.TIMELINE_USER_NOT_FOUND, message);
    }

    public UserNotFoundException(String code, String message) {
        this(code, message, null, false, null);
    }

    public UserNotFoundException(String code, String message, ServiceExceptionResponse response, boolean printStackTrace, Throwable ex) {
        super(code,message,response,printStackTrace,ex);
    }
}
