package it.gov.pagopa.timeline.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationDTO {

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

  private String status;

  private LocalDateTime operationDate;

  private Long amountCents;

  private Long accruedCents;

  private String businessName;

  private String instrumentType;

}

