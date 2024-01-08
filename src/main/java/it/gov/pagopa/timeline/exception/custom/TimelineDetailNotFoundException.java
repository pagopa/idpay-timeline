package it.gov.pagopa.timeline.exception.custom;

import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.common.web.exception.ServiceExceptionPayload;
import it.gov.pagopa.timeline.constants.TimelineConstants;

public class TimelineDetailNotFoundException extends ServiceException {


    public TimelineDetailNotFoundException(String message) {
        this(TimelineConstants.ExceptionCode.TIMELINE_DETAIL_NOT_FOUND, message);
    }

    public TimelineDetailNotFoundException(String code, String message) {
        this(code, message, null, false, null);
    }

    public TimelineDetailNotFoundException(String code, String message, ServiceExceptionPayload payload, boolean printStackTrace, Throwable ex) {
        super(code, message, payload, printStackTrace, ex);
    }
}
