package it.gov.pagopa.timeline.dto.mapper;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.OperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.model.Operation;
import org.springframework.stereotype.Service;

@Service
public class OperationMapper {

  public Operation toOperation(QueueOperationDTO queueOperationDTO) {
    Operation operation = new Operation();
    operation.setInitiativeId(queueOperationDTO.getInitiativeId());
    operation.setUserId(queueOperationDTO.getUserId());
    operation.setOperationType(queueOperationDTO.getOperationType());
    operation.setHpan(queueOperationDTO.getHpan());
    operation.setCircuitType(queueOperationDTO.getCircuitType());
    operation.setIban(queueOperationDTO.getIban());
    operation.setChannel(queueOperationDTO.getChannel());
    operation.setOperationDate(queueOperationDTO.getOperationDate());
    operation.setAmount(queueOperationDTO.getAmount());
    operation.setEffectiveAmount(queueOperationDTO.getEffectiveAmount());
    operation.setAccrued(queueOperationDTO.getAccrued());
    operation.setIdTrxIssuer(queueOperationDTO.getIdTrxIssuer());
    operation.setIdTrxAcquirer(queueOperationDTO.getIdTrxAcquirer());

    return operation;
  }

  public OperationDTO toOperationDTO(Operation operation) {
    return OperationDTO.builder()
        .operationId(operation.getOperationId())
        .operationType(operation.getOperationType())
        .operationDate(operation.getOperationDate())
        .amount(operation.getAmount())
        .channel(operation.getChannel())
        .circuitType(operation.getCircuitType())
        .hpan(operation.getHpan())
        .iban(operation.getIban())
        .build();
  }

  public DetailOperationDTO toDetailOperationDTO(Operation operation) {
    return DetailOperationDTO.builder()
        .operationId(operation.getOperationId())
        .operationType(operation.getOperationType())
        .operationDate(operation.getOperationDate())
        .amount(operation.getAmount())
        .accrued(operation.getAccrued())
        .channel(operation.getChannel())
        .circuitType(operation.getCircuitType())
        .hpan(operation.getHpan())
        .iban(operation.getIban())
        .idTrxAcquirer(operation.getIdTrxAcquirer())
        .idTrxIssuer(operation.getIdTrxIssuer())
        .build();
  }
}
