package it.gov.pagopa.timeline.exception.custom;

import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.common.web.exception.ServiceExceptionPayload;
import it.gov.pagopa.timeline.constants.TimelineConstants;

public class RefundsNotFoundException extends ServiceException {


    public RefundsNotFoundException(String message) {
        this(TimelineConstants.ExceptionCode.TIMELINE_REFUNDS_NOT_FOUND, message);
    }

    public RefundsNotFoundException(String code, String message) {
        this(code, message, null, false, null);
    }

    public RefundsNotFoundException(String code, String message, ServiceExceptionPayload response, boolean printStackTrace, Throwable ex) {
        super(code,message,response,printStackTrace,ex);
    }
}
