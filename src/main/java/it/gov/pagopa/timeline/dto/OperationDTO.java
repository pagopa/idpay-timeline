package it.gov.pagopa.timeline.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationDTO {

  private String operationId;

  private String operationType;

  private String hpan;

  private String circuitType;

  private String iban;

  private String channel;

  private String operationDate;

  private String amount;

}

