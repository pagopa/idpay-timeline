package it.gov.pagopa.timeline.exception.custom;

import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.timeline.constants.TimelineConstants;

public class RefundsNotFoundException extends ServiceException {


    public RefundsNotFoundException(String message) {
        this(TimelineConstants.TIMELINE_REFUNDS_NOT_FOUND, message);
    }

    public RefundsNotFoundException(String code, String message) {
        this(code, message, false, null);
    }

    public RefundsNotFoundException(String code, String message, boolean printStackTrace, Throwable ex) {
        super(code,message,printStackTrace,ex);
    }
}
