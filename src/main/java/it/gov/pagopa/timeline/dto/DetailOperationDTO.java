package it.gov.pagopa.timeline.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailOperationDTO {

  private String operationId;

  private String operationType;

  private String eventId;

  private String brandLogo;

  private String brand;

  private String maskedPan;

  private String instrumentId;

  private String circuitType;

  private String iban;

  private String channel;

  private LocalDateTime operationDate;

  private BigDecimal amount;

  private BigDecimal accrued;

  private String idTrxIssuer;

  private String idTrxAcquirer;

  private String status;

  private String refundType;

  private LocalDate startDate;

  private LocalDate endDate;

  private LocalDate transferDate;

  private LocalDate userNotificationDate;

  private String cro;

  private String businessName;

}

