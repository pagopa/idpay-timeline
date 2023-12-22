package it.gov.pagopa.timeline.exception.custom;

import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.common.web.exception.ServiceExceptionPayload;
import it.gov.pagopa.timeline.constants.TimelineConstants;

public class UserNotFoundException extends ServiceException {


    public UserNotFoundException(String message) {
        this(TimelineConstants.ExceptionCode.TIMELINE_USER_NOT_FOUND, message);
    }

    public UserNotFoundException(String code, String message) {
        this(code, message, null, false, null);
    }

    public UserNotFoundException(String code, String message, ServiceExceptionPayload payload, boolean printStackTrace, Throwable ex) {
        super(code, message, payload, printStackTrace, ex);
    }
}
