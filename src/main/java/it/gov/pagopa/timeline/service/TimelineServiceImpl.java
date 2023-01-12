package it.gov.pagopa.timeline.service;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.OperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import it.gov.pagopa.timeline.dto.mapper.OperationMapper;
import it.gov.pagopa.timeline.event.producer.TimelineProducer;
import it.gov.pagopa.timeline.exception.TimelineException;
import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.repository.TimelineRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    Operation operation = timelineRepository.findByInitiativeIdAndOperationIdAndUserId(
        initiativeId, operationId, userId).orElseThrow(
        () ->
            new TimelineException(
                HttpStatus.NOT_FOUND.value(), "Cannot find the requested operation!"));
    return operationMapper.toDetailOperationDTO(operation);
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
        operationList.add(operationMapper.toOperationDTO(operation))
    );

    LocalDateTime lastUpdate = operationList.get(0).getOperationDate();

    if (page != 0) {
      Operation first = timelineRepository.findFirstByInitiativeIdAndUserIdOrderByOperationDateDesc(
          initiativeId, userId).orElse(null);
      if(first != null){
        lastUpdate = first.getOperationDate();
      }
    }

    return new TimelineDTO(lastUpdate, operationList);
  }

  @Override
  public void sendToQueue(QueueOperationDTO queueOperationDTO) {
    timelineProducer.sendOperation(queueOperationDTO);
  }

  @Override
  public void saveOperation(QueueOperationDTO queueOperationDTO) {
    Operation operation = operationMapper.toOperation(queueOperationDTO);
    timelineRepository.save(operation);
  }

  @Override
  public TimelineDTO getRefunds(String initiativeId, String userId) {
    List<Operation> timeline = timelineRepository.findByInitiativeIdAndUserIdAndOperationTypeContainingOrderByOperationDateDesc(
        initiativeId, userId, "REFUND");

    List<OperationDTO> operationList = new ArrayList<>();
    if (timeline.isEmpty()) {
      throw new TimelineException(HttpStatus.NOT_FOUND.value(),
          "No refunds have been rewarded on this initiative!");
    }
    timeline.forEach(operation ->
        operationList.add(operationMapper.toOperationDTO(operation))
    );
    return new TimelineDTO(operationList.get(0).getOperationDate(), operationList);
  }

}
