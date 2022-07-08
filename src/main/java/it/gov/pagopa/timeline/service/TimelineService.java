package it.gov.pagopa.timeline.service;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;

public interface TimelineService {
  DetailOperationDTO getTimelineDetail(String initiativeId, String operationId, String userId);
}
