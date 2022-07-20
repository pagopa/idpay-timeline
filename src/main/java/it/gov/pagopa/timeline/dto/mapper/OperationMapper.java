package it.gov.pagopa.timeline.dto.mapper;

import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.model.Operation;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class OperationMapper {
  public Operation map(QueueOperationDTO queueOperationDTO){
    Operation operation = null;

    if(queueOperationDTO != null){
      operation = new Operation();
      operation.setInitiativeId(queueOperationDTO.getInitiativeId());
      operation.setUserId(queueOperationDTO.getUserId());
      operation.setOperationType(queueOperationDTO.getOperationType());
      operation.setHpan(queueOperationDTO.getHpan());
      operation.setCircuitType(queueOperationDTO.getCircuitType());
      operation.setIban(queueOperationDTO.getIban());
      operation.setChannel(queueOperationDTO.getChannel());
      operation.setOperationDate(queueOperationDTO.getOperationDate());
      if(queueOperationDTO.getAmount() != null) {
        operation.setAmount(new BigDecimal(queueOperationDTO.getAmount()));
      }
      if(queueOperationDTO.getAccrued() != null) {
        operation.setAccrued(new BigDecimal(queueOperationDTO.getAccrued()));
      }
      operation.setIdTrxIssuer(queueOperationDTO.getIdTrxIssuer());
      operation.setIdTrxAcquirer(queueOperationDTO.getIdTrxAcquirer());
    }

    return operation;
  }
}
