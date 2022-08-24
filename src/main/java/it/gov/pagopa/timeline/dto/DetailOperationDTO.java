package it.gov.pagopa.timeline.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailOperationDTO {

  private String operationId;

  private String operationType;

  private String hpan;

  private String circuitType;

  private String iban;

  private String channel;

  private String email;

  private String operationDate;

  private String amount;

  private String accrued;

  private String idTrxIssuer;

  private String idTrxAcquirer;

}

