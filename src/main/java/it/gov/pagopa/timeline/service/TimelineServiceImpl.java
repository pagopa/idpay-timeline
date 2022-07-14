package it.gov.pagopa.timeline.service;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.OperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import it.gov.pagopa.timeline.dto.mapper.OperationMapper;
import it.gov.pagopa.timeline.event.TimelineProducer;
import it.gov.pagopa.timeline.exception.TimelineException;
import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.repository.TimelineRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TimelineServiceImpl implements TimelineService {

  @Autowired
  TimelineRepository timelineRepository;

  @Autowired
  OperationMapper operationMapper;

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
  public TimelineDTO getTimeline(String initiativeId, String userId, String operationType, int page,
      int size) {

    Pageable paging = PageRequest.of(page, size, Sort.Direction.DESC, "operationDate");

    Operation operationExample = new Operation();
    operationExample.setInitiativeId(initiativeId);
    operationExample.setUserId(userId);
    operationExample.setOperationType(operationType);

    List<Operation> timeline = timelineRepository.findAll(Example.of(operationExample), paging)
        .toList();

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
  public void sendToQueue(QueueOperationDTO queueOperationDTO) {
    timelineProducer.sendOperation(queueOperationDTO);
  }

  @Override
  public void saveOperation(QueueOperationDTO queueOperationDTO) {
    Operation operation = operationMapper.map(queueOperationDTO);
    timelineRepository.save(operation);
  }

  private DetailOperationDTO operationToDetailDto(Operation operation) {
    ModelMapper modelmapper = new ModelMapper();
    return modelmapper.map(operation, DetailOperationDTO.class);
  }

  private OperationDTO operationToOperationDto(Operation operation) {
    ModelMapper modelmapper = new ModelMapper();
    return modelmapper.map(operation, OperationDTO.class);
  }
}
