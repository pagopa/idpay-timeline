package it.gov.pagopa.timeline.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailOperationDTO {

  private String operationId;

  private String operationType;

  private String hpan;

  private String circuitType;

  private String iban;

  private String channel;

  private LocalDateTime operationDate;

  private BigDecimal amount;

  private BigDecimal accrued;

  private String idTrxIssuer;

  private String idTrxAcquirer;

}

