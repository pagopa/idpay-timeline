package it.gov.pagopa.timeline.controller;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimelineControllerImpl implements TimelineController {

  private final TimelineService timelineService;

  public TimelineControllerImpl(TimelineService timelineService) {
    this.timelineService = timelineService;
  }

  @Override
  public ResponseEntity<DetailOperationDTO> getTimelineDetail(String initiativeId,
      String operationId, String userId) {
    DetailOperationDTO operationDTO = timelineService.getTimelineDetail(initiativeId, operationId,
        userId);
    return new ResponseEntity<>(operationDTO, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<TimelineDTO> getTimeline(String initiativeId, String userId,
      String operationType, Integer page, Integer size, LocalDateTime dateFrom, LocalDateTime dateTo) {
    TimelineDTO timelineDTO = timelineService.getTimeline(initiativeId, userId, operationType, page,
        size, dateFrom, dateTo);
    return new ResponseEntity<>(timelineDTO, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> addOperation(QueueOperationDTO body) {
    timelineService.sendToQueue(body);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<TimelineDTO> getRefunds(String initiativeId, String userId) {
    TimelineDTO timelineDTO = timelineService.getRefunds(initiativeId, userId);
    return new ResponseEntity<>(timelineDTO, HttpStatus.OK);
  }
}
