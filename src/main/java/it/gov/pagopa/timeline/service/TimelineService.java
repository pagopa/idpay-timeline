package it.gov.pagopa.timeline.service;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;

public interface TimelineService {

  DetailOperationDTO getTimelineDetail(String initiativeId, String operationId, String userId);

  TimelineDTO getTimeline(String initiativeId, String userId, String operationType, int page,
      int size);

  void sendToQueue(QueueOperationDTO queueOperationDTO);

  void saveOperation(QueueOperationDTO queueOperationDTO);

  TimelineDTO getRefunds(String initiativeId, String userId);
}
