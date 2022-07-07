package it.gov.pagopa.timeline.service;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.PutOperationDTO;

public interface TimelineService {

  DetailOperationDTO getTimelineDetail(String initiativeId, String operationId, String userId);

  void sendToQueue(PutOperationDTO putOperationDTO);
}
