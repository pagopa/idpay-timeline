package it.gov.pagopa.timeline.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "timeline")
@CompoundIndex(name = "timeline_idx", def = "{'userId': 1, 'initiativeId': 1}")
public class Operation {

  @Id
  private String operationId;

  private String userId;

  private String initiativeId;

  private String operationType;

  private String hpan;

  private String circuitType;

  private String iban;

  private String channel;

  private String email;

  private LocalDateTime operationDate;

  private BigDecimal amount;

  private BigDecimal accrued;

  private String idTrxIssuer;

  private String idTrxAcquirer;

}
