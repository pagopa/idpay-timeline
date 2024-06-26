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
        operation.setInstrumentType(queueOperationDTO.getInstrumentType());
        operation.setRewardNotificationId(queueOperationDTO.getRewardNotificationId());
        operation.setCro(queueOperationDTO.getCro());
        operation.setRewardFeedbackProgressive(queueOperationDTO.getRewardFeedbackProgressive());
        operation.setOperationDate(queueOperationDTO.getOperationDate());
        operation.setAmountCents(queueOperationDTO.getAmountCents());
        operation.setEffectiveAmountCents(queueOperationDTO.getEffectiveAmountCents());
        operation.setAccruedCents(queueOperationDTO.getAccruedCents());
        operation.setIdTrxIssuer(queueOperationDTO.getIdTrxIssuer());
        operation.setIdTrxAcquirer(queueOperationDTO.getIdTrxAcquirer());
        operation.setStatus(queueOperationDTO.getStatus());
        operation.setRefundType(queueOperationDTO.getRefundType());
        operation.setStartDate(queueOperationDTO.getStartDate());
        operation.setEndDate(queueOperationDTO.getEndDate());
        operation.setTransferDate(queueOperationDTO.getTransferDate());
        operation.setUserNotificationDate(queueOperationDTO.getUserNotificationDate());
        operation.setBusinessName(queueOperationDTO.getBusinessName());
        return operation;
    }

    public OperationDTO toOperationDTO(Operation operation) {
        return OperationDTO.builder()
                .operationId(operation.getOperationId())
                .operationType(operation.getOperationType())
                .eventId(operation.getEventId())
                .operationDate(operation.getOperationDate())
                .amountCents(operation.getEffectiveAmountCents())
                .accruedCents(operation.getAccruedCents())
                .channel(operation.getChannel())
                .circuitType(operation.getCircuitType())
                .maskedPan(operation.getMaskedPan())
                .instrumentId(operation.getInstrumentId())
                .brandLogo(operation.getBrandLogo())
                .brand(operation.getBrand())
                .iban(operation.getIban())
                .status(operation.getStatus())
                .businessName(operation.getBusinessName())
                .instrumentType(operation.getInstrumentType())
                .build();
    }

    public DetailOperationDTO toDetailOperationDTO(Operation operation) {
        return DetailOperationDTO.builder()
                .operationId(operation.getOperationId())
                .operationType(operation.getOperationType())
                .eventId(operation.getEventId())
                .operationDate(operation.getOperationDate())
                .amountCents(operation.getEffectiveAmountCents())
                .accruedCents(operation.getAccruedCents())
                .channel(operation.getChannel())
                .circuitType(operation.getCircuitType())
                .maskedPan(operation.getMaskedPan())
                .instrumentId(operation.getInstrumentId())
                .instrumentType(operation.getInstrumentType())
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
                .cro(operation.getCro())
                .businessName(operation.getBusinessName())
                .build();
    }
}
