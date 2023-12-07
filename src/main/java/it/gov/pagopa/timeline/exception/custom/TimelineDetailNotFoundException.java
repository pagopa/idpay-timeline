package it.gov.pagopa.timeline.exception.custom;

import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.timeline.constants.TimelineConstants;

public class TimelineDetailNotFoundException extends ServiceException {


    public TimelineDetailNotFoundException(String message) {
        this(TimelineConstants.TIMELINE_DETAIL_NOT_FOUND, message);
    }

    public TimelineDetailNotFoundException(String code, String message) {
        this(code, message, false, null);
    }

    public TimelineDetailNotFoundException(String code, String message, boolean printStackTrace, Throwable ex) {
        super(code,message,printStackTrace,ex);
    }
}
