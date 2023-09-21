package it.gov.pagopa.timeline.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.gov.pagopa.timeline.constants.TimelineConstants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueueOperationDTO {

  @NotBlank(message = TimelineConstants.ERROR_MANDATORY_FIELD)
  private String userId;

  @NotBlank(message = TimelineConstants.ERROR_MANDATORY_FIELD)
  private String initiativeId;

  @NotBlank(message = TimelineConstants.ERROR_MANDATORY_FIELD)
  private String operationType;

  private String rewardNotificationId;

  /**
   * Id from reward-notification identifying a single refund
   */
  private String eventId;

  private String brandLogo;

  private String brand;

  private String maskedPan;

  private String instrumentId;

  private String circuitType;

  private String iban;

  private String channel;

  private String instrumentType;

  private String cro;

  private Long rewardFeedbackProgressive;

  private LocalDateTime operationDate;

  private BigDecimal amount;

  private BigDecimal effectiveAmount;

  private BigDecimal accrued;

  private String idTrxIssuer;

  private String idTrxAcquirer;

  private String status;

  private String refundType;

  private LocalDate startDate;

  private LocalDate endDate;

  private LocalDate transferDate;

  private LocalDate userNotificationDate;

  private String businessName;

}

