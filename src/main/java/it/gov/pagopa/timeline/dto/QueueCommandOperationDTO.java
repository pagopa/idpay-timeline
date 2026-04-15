package it.gov.pagopa.timeline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class QueueCommandOperationDTO {
    private String operationType;
    private String entityId;
    private Instant operationTime;
    private Map<String, String> additionalParams;
}
