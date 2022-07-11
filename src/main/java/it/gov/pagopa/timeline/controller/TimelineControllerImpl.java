package it.gov.pagopa.timeline.controller;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.PutOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimelineControllerImpl implements TimelineController {

  @Autowired
  TimelineService timelineService;

  @Override
  public ResponseEntity<DetailOperationDTO> getTimelineDetail(String initiativeId,
      String operationId, String userId) {
    DetailOperationDTO operationDTO = timelineService.getTimelineDetail(initiativeId, operationId,
        userId);
    return new ResponseEntity<>(operationDTO, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<TimelineDTO> getTimeline(String initiativeId, String userId,
      String operationType, Integer page, Integer size) {
    TimelineDTO timelineDTO = timelineService.getTimeline(initiativeId, userId, operationType, page,
        size);
    return new ResponseEntity<>(timelineDTO, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> addOperation(PutOperationDTO body) {
    timelineService.sendToQueue(body);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
