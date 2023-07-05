package it.gov.pagopa.timeline.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@FieldNameConstants
@Document(collection = "timeline")
@CompoundIndex(name = "timeline_idx", def = "{'userId': 1, 'initiativeId': 1}")
public class Operation {

  @Id
  private String operationId;

  private String userId;

  private String initiativeId;

  private String operationType;

  private String eventId;

  private String brandLogo;

  private String brand;

  private String maskedPan;

  private String instrumentId;

  private String circuitType;

  private String iban;

  private String channel;

  private String rewardNotificationId;

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
