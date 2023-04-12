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
        operation.setEventId(queueOperationDTO.getEventId());
        operation.setMaskedPan(queueOperationDTO.getMaskedPan());
        operation.setBrandLogo(queueOperationDTO.getBrandLogo());
        operation.setBrand(queueOperationDTO.getBrand());
        operation.setInstrumentId(queueOperationDTO.getInstrumentId());
        operation.setCircuitType(queueOperationDTO.getCircuitType());
        operation.setIban(queueOperationDTO.getIban());
        operation.setChannel(queueOperationDTO.getChannel());
        operation.setRewardNotificationId(queueOperationDTO.getRewardNotificationId());
        operation.setCro(queueOperationDTO.getCro());
        operation.setRewardFeedbackProgressive(queueOperationDTO.getRewardFeedbackProgressive());
        operation.setOperationDate(queueOperationDTO.getOperationDate());
        operation.setAmount(queueOperationDTO.getAmount());
        operation.setEffectiveAmount(queueOperationDTO.getEffectiveAmount());
        operation.setAccrued(queueOperationDTO.getAccrued());
        operation.setIdTrxIssuer(queueOperationDTO.getIdTrxIssuer());
        operation.setIdTrxAcquirer(queueOperationDTO.getIdTrxAcquirer());
        operation.setStatus(queueOperationDTO.getStatus());
        operation.setRefundType(queueOperationDTO.getRefundType());
        operation.setStartDate(queueOperationDTO.getStartDate());
        operation.setEndDate(queueOperationDTO.getEndDate());
        operation.setTransferDate(queueOperationDTO.getTransferDate());
        operation.setUserNotificationDate(queueOperationDTO.getUserNotificationDate());

        return operation;
    }

    public OperationDTO toOperationDTO(Operation operation) {
        return OperationDTO.builder()
                .operationId(operation.getOperationId())
                .operationType(operation.getOperationType())
                .eventId(operation.getEventId())
                .operationDate(operation.getOperationDate())
                .amount(operation.getAmount())
                .accrued(operation.getAccrued())
                .channel(operation.getChannel())
                .circuitType(operation.getCircuitType())
                .maskedPan(operation.getMaskedPan())
                .instrumentId(operation.getInstrumentId())
                .brandLogo(operation.getBrandLogo())
                .brand(operation.getBrand())
                .iban(operation.getIban())
                .build();
    }

    public DetailOperationDTO toDetailOperationDTO(Operation operation) {
        return DetailOperationDTO.builder()
                .operationId(operation.getOperationId())
                .operationType(operation.getOperationType())
                .eventId(operation.getEventId())
                .operationDate(operation.getOperationDate())
                .amount(operation.getAmount())
                .accrued(operation.getAccrued())
                .channel(operation.getChannel())
                .circuitType(operation.getCircuitType())
                .maskedPan(operation.getMaskedPan())
                .instrumentId(operation.getInstrumentId())
                .brandLogo(operation.getBrandLogo())
                .brand(operation.getBrand())
                .iban(operation.getIban())
                .idTrxAcquirer(operation.getIdTrxAcquirer())
                .idTrxIssuer(operation.getIdTrxIssuer())
                .status(operation.getStatus())
                .refundType(operation.getRefundType())
                .startDate(operation.getStartDate())
                .endDate(operation.getEndDate())
                .transferDate(operation.getTransferDate())
                .userNotificationDate(operation.getUserNotificationDate())
                .build();
    }
}
