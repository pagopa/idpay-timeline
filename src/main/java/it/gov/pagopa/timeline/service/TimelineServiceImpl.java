package it.gov.pagopa.timeline.service;

import it.gov.pagopa.timeline.constants.TimelineConstants;
import it.gov.pagopa.timeline.dto.*;
import it.gov.pagopa.timeline.dto.mapper.OperationMapper;
import it.gov.pagopa.timeline.enums.ChannelTransaction;
import it.gov.pagopa.timeline.event.producer.TimelineProducer;
import it.gov.pagopa.timeline.exception.custom.RefundsNotFoundException;
import it.gov.pagopa.timeline.exception.custom.TimelineDetailNotFoundException;
import it.gov.pagopa.timeline.exception.custom.UserNotFoundException;
import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.repository.TimelineRepository;
import it.gov.pagopa.timeline.utils.AuditUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@SuppressWarnings("BusyWait")
public class TimelineServiceImpl implements TimelineService {

  @Autowired
  TimelineRepository timelineRepository;

  @Autowired
  OperationMapper operationMapper;

  @Autowired
  TimelineProducer timelineProducer;
  @Autowired
  AuditUtilities auditUtilities;

  private static final Set<Pair<String, String>> ignoreCombinations = Set.of(
      Pair.of(TimelineConstants.TRX_STATUS_AUTHORIZED, TimelineConstants.TRX_STATUS_REWARDED),
      Pair.of(TimelineConstants.TRX_STATUS_AUTHORIZED , TimelineConstants.TRX_STATUS_AUTHORIZED),
      Pair.of(TimelineConstants.TRX_STATUS_REWARDED, TimelineConstants.TRX_STATUS_REWARDED)
  );

  @Value("${app.delete.paginationSize}")
  private int pageSize;
  @Value("${app.delete.delayTime}")
  private long delay;

  @Override
  public DetailOperationDTO getTimelineDetail(String initiativeId, String operationId,
      String userId) {
    long startTime = System.currentTimeMillis();

    Operation operation = timelineRepository.findByInitiativeIdAndOperationIdAndUserId(
        initiativeId, operationId, userId).orElseThrow(
        () ->
            new TimelineDetailNotFoundException("Cannot find the detail of timeline on initiative [%s]".formatted(initiativeId)));
    performanceLog(startTime, "GET_TIMELINE_DETAIL");
    return operationMapper.toDetailOperationDTO(operation);
  }

  @Override
  public TimelineDTO getTimeline(String initiativeId, String userId, String operationType, int page,
      int size, LocalDateTime startDate, LocalDateTime endDate) {

    long startTime = System.currentTimeMillis();

    Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "operationDate");
    Criteria criteria = timelineRepository.getCriteria(initiativeId, userId, operationType,
        startDate,
        endDate);
    List<OperationDTO> operationListDTO = new ArrayList<>();
    List<Operation> operationList = timelineRepository.findByFilter(criteria, pageable);

    if (operationList.isEmpty()){
      throw new UserNotFoundException("Timeline for the current user and initiative [%s] was not found".formatted(initiativeId));
    }

    long count = timelineRepository.getCount(criteria);
    final Page<Operation> result = PageableExecutionUtils.getPage(operationList, pageable,
        () -> count);
    for (Operation operation : operationList) {
      operationListDTO.add(operationMapper.toOperationDTO(operation));
    }
    LocalDateTime lastUpdate =
        !operationList.isEmpty() ? operationList.get(0).getOperationDate() : null;
    if (page != 0) {
      Operation first = timelineRepository.findFirstByInitiativeIdAndUserIdOrderByOperationDateDesc(
          initiativeId, userId).orElse(null);
      if (first != null) {
        lastUpdate = first.getOperationDate();
      }
    }
    performanceLog(startTime, "GET_TIMELINE_LIST");
    return new TimelineDTO(lastUpdate, operationListDTO, result.getNumber(), result.getSize(),
        (int) result.getTotalElements(), result.getTotalPages());
  }

  @Override
  public void sendToQueue(QueueOperationDTO queueOperationDTO) {
    timelineProducer.sendOperation(queueOperationDTO);
  }

  @Override
  public void saveOperation(QueueOperationDTO queueOperationDTO) {
    long startTime = System.currentTimeMillis();

    if (ChannelTransaction.isChannelPresent(queueOperationDTO.getChannel())
    && TimelineConstants.OPERATION_TYPE_TRX.equals(queueOperationDTO.getOperationType())) {
      Optional<Operation> existingOperation = timelineRepository.findByEventId(
          queueOperationDTO.getEventId());

      if (existingOperation.isPresent()) {
        Operation operation = existingOperation.get();
        if (ignoreCombinations.contains(Pair.of(queueOperationDTO.getStatus(), operation.getStatus()))) {
          return;
        }
        else if (TimelineConstants.TRX_STATUS_REWARDED.equals(queueOperationDTO.getStatus())
            && TimelineConstants.TRX_STATUS_AUTHORIZED.equals(operation.getStatus())) {
          timelineRepository.updateOperationStatusByEventId(
              queueOperationDTO.getEventId(),
              queueOperationDTO.getStatus());
          performanceLog(startTime, "UPDATE_OPERATION");
          return;
        }
      }
    }

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
      throw new RefundsNotFoundException("No refunds have been rewarded for the current user and initiative [%s]".formatted(initiativeId));
    }
    timeline.forEach(operation ->
        operationList.add(operationMapper.toOperationDTO(operation))
    );
    performanceLog(startTime, "GET_REFUNDS");
    return new TimelineDTO(operationList.get(0).getOperationDate(), operationList, 0, 0, 0, 0);
  }

  @Override
  public void processOperation(QueueCommandOperationDTO queueCommandOperationDTO) {
    if (TimelineConstants.OPERATION_TYPE_DELETE_INITIATIVE.equals(queueCommandOperationDTO.getOperationType())) {
      long startTime = System.currentTimeMillis();

      List<Operation> deletedOperation = new ArrayList<>();
      List<Operation> fetchedOperations;

      do {
        fetchedOperations = timelineRepository.deletePaged(queueCommandOperationDTO.getEntityId(), pageSize);
        deletedOperation.addAll(fetchedOperations);
        try{
          Thread.sleep(delay);
        } catch (InterruptedException e){
          log.error("An error has occurred while waiting {}", e.getMessage());
          Thread.currentThread().interrupt();
        }
      } while (fetchedOperations.size() == pageSize);

      log.info("[DELETE_INITIATIVE] Deleted initiative {} from collection: timeline", queueCommandOperationDTO.getEntityId());

      List<String> usersId = deletedOperation.stream().map(Operation::getUserId).distinct().toList();
      usersId.forEach(userId -> auditUtilities.logDeleteOperation(userId, queueCommandOperationDTO.getEntityId()));
      performanceLog(startTime, "DELETE_INITIATIVE");
    }
  }

    private void performanceLog(long startTime, String service) {
        log.info(
                "[PERFORMANCE_LOG] [{}] Time occurred to perform business logic: {} ms",
                service,
                System.currentTimeMillis() - startTime);
    }

}
