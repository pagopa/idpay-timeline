package it.gov.pagopa.timeline.service;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.OperationDTO;
import it.gov.pagopa.timeline.dto.PutOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import it.gov.pagopa.timeline.event.TimelineProducer;
import it.gov.pagopa.timeline.exception.TimelineException;
import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.repository.TimelineRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TimelineServiceImpl implements TimelineService {

  @Autowired
  TimelineRepository timelineRepository;

  @Autowired
  TimelineProducer timelineProducer;

  @Override
  public DetailOperationDTO getTimelineDetail(String initiativeId, String operationId,
      String userId) {
    Optional<Operation> operation = timelineRepository.findByInitiativeIdAndOperationIdAndUserId(
        initiativeId, operationId, userId);
    return operation.map(this::operationToDetailDto).orElseThrow(
        () ->
            new TimelineException(
                HttpStatus.NOT_FOUND.value(), "Cannot find the requested operation!"));
  }

  @Override
  public TimelineDTO getTimeline(String initiativeId, String userId) {
    List<Operation> timeline = timelineRepository.findByInitiativeIdAndUserIdOrderByOperationDateDesc(initiativeId, userId);
    List<OperationDTO> operationList = new ArrayList<>();
    if (timeline.isEmpty()) {
      throw new TimelineException(HttpStatus.NOT_FOUND.value(),
          "No operations have been made on this initiative!");
    }
    timeline.forEach(operation ->
        operationList.add(operationToOperationDto(operation))
    );
    return new TimelineDTO(operationList.get(0).getOperationDate(), operationList);
  }

  @Override
  public void sendToQueue(PutOperationDTO putOperationDTO) {
    timelineProducer.sendOperation(putOperationDTO);
  }

  private DetailOperationDTO operationToDetailDto(Operation operation) {
    ModelMapper modelmapper = new ModelMapper();
    return operation != null ? modelmapper.map(operation, DetailOperationDTO.class) : null;
  }

  private OperationDTO operationToOperationDto(Operation operation) {
    ModelMapper modelmapper = new ModelMapper();
    return operation != null ? modelmapper.map(operation, OperationDTO.class) : null;
  }
}
