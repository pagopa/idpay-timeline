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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
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
    long startTime = System.currentTimeMillis();

    Operation operation = timelineRepository.findByInitiativeIdAndOperationIdAndUserId(
        initiativeId, operationId, userId).orElseThrow(
        () ->
            new TimelineException(
                HttpStatus.NOT_FOUND.value(), "Cannot find the requested operation!"));
    performanceLog(startTime, "GET_TIMELINE_DETAIL");
    return operationMapper.toDetailOperationDTO(operation);
  }

  @Override
  public TimelineDTO getTimeline(String initiativeId, String userId, String operationType, int page,
      int size, LocalDateTime startDate, LocalDateTime endDate) {

    long startTime = System.currentTimeMillis();

    Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "operationDate");
    Criteria criteria = timelineRepository.getCriteria(initiativeId, userId, operationType, startDate,
            endDate);
    List<OperationDTO> operationListDTO = new ArrayList<>();
    List<Operation> operationList = timelineRepository.findByFilter(criteria, pageable);
    long count = timelineRepository.getCount(criteria);
    final Page<Operation> result = PageableExecutionUtils.getPage(operationList,pageable, () -> count);
    for (Operation operation : operationList) {
      operationListDTO.add(operationMapper.toOperationDTO(operation));
    }
    LocalDateTime lastUpdateDate = !operationListDTO.isEmpty() ? operationListDTO.get(0).getOperationDate() : null;
    performanceLog(startTime, "GET_TIMELINE_LIST");
    return new TimelineDTO(lastUpdateDate, operationListDTO,result.getNumber(),result.getSize(),(int)result.getTotalElements(),result.getTotalPages());
  }

  @Override
  public void sendToQueue(QueueOperationDTO queueOperationDTO) {
    timelineProducer.sendOperation(queueOperationDTO);
  }

  @Override
  public void saveOperation(QueueOperationDTO queueOperationDTO) {
    long startTime = System.currentTimeMillis();

    Operation operation = operationMapper.toOperation(queueOperationDTO);
    timelineRepository.save(operation);

    performanceLog(startTime, "SAVE_OPERATION");
  }

  @Override
  public TimelineDTO getRefunds(String initiativeId, String userId) {
    long startTime = System.currentTimeMillis();

    List<Operation> timeline = timelineRepository.findByInitiativeIdAndUserIdAndOperationTypeContainingOrderByOperationDateDesc(
        initiativeId, userId, "REFUND");

    List<OperationDTO> operationList = new ArrayList<>();
    if (timeline.isEmpty()) {
      performanceLog(startTime, "GET_REFUNDS");
      throw new TimelineException(HttpStatus.NOT_FOUND.value(),
          "No refunds have been rewarded on this initiative!");
    }
    timeline.forEach(operation ->
        operationList.add(operationMapper.toOperationDTO(operation))
    );
    performanceLog(startTime, "GET_REFUNDS");
    return new TimelineDTO(operationList.get(0).getOperationDate(), operationList,0,0,0,0);
  }

  private void performanceLog(long startTime, String service) {
    log.info(
        "[PERFORMANCE_LOG] [{}] Time occurred to perform business logic: {} ms",
        service,
        System.currentTimeMillis() - startTime);
  }

}
