package it.gov.pagopa.timeline.constants;

public class TimelineConstants {

  public static final String ERROR_MANDATORY_FIELD = "The field is mandatory!";
  public static final String TRX_STATUS_AUTHORIZED = "AUTHORIZED";
  public static final String TRX_STATUS_REWARDED = "REWARDED";
  public static final String TRX_STATUS_CANCELLED = "CANCELLED";
  public static final String CHANNEL_QRCODE = "QRCODE";
  public static final String OPERATION_TYPE_TRX = "TRANSACTION";
  public static final String OPERATION_TYPE_DELETE_INITIATIVE = "DELETE_INITIATIVE";
  private TimelineConstants(){}

  public static final class Exception extends AbstractConstant {
    public static final class BadRequest {
      public static final String CODE = BASE_CODE + ".bad.request";
    }

    public static final class NotFound {
      public static final String CODE = BASE_CODE + ".not.found";
    }
  }
}
