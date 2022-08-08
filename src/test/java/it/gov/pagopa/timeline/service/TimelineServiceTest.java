package it.gov.pagopa.timeline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.OperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import it.gov.pagopa.timeline.dto.mapper.OperationMapper;
import it.gov.pagopa.timeline.event.TimelineProducer;
import it.gov.pagopa.timeline.exception.TimelineException;
import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.repository.TimelineRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TimelineServiceImpl.class)
class TimelineServiceTest {

  @MockBean
  TimelineRepository timelineRepositoryMock;

  @MockBean
  OperationMapper operationMapper;

  @Autowired
  TimelineService timelineService;

  @MockBean
  TimelineProducer timelineProducer;

  private static final String USER_ID = "TEST_USER_ID";
  private static final String INITIATIVE_ID = "TEST_INITIATIVE_ID";
  private static final String OPERATION_ID = "TEST_OPERATION_ID";
  private static final String HPAN = "TEST_HPAN";
  private static final String IBAN = "TEST_IBAN";
  private static final String CIRCUIT_TYPE = "00";
  private static final LocalDateTime OPERATION_DATE = LocalDateTime.now();
  private static final BigDecimal AMOUNT = new BigDecimal("50.00");
  private static final Operation OPERATION = new Operation();
  private static final String OPERATION_TYPE = "PAID_REFUND";
  private static final String CHANNEL = "APP_IO";

  private static final QueueOperationDTO QUEUE_OPERATION_DTO = new QueueOperationDTO(
      USER_ID, INITIATIVE_ID, OPERATION_TYPE, null, null, null, null, null, null, null, null, null, null);

  static {
    OPERATION.setOperationType(OPERATION_TYPE);
    OPERATION.setInitiativeId(INITIATIVE_ID);
    OPERATION.setUserId(USER_ID);
    OPERATION.setHpan(HPAN);
    OPERATION.setIban(IBAN);
    OPERATION.setOperationDate(OPERATION_DATE);
    OPERATION.setAmount(AMOUNT);
    OPERATION.setCircuitType(CIRCUIT_TYPE);
    OPERATION.setChannel(CHANNEL);
  }

  @Test
  void getTimelineDetail_ok() {
    Mockito.when(timelineRepositoryMock.findByInitiativeIdAndOperationIdAndUserId(INITIATIVE_ID,
            OPERATION_ID, USER_ID))
        .thenReturn(Optional.of(OPERATION));

    try {
      DetailOperationDTO actual = timelineService.getTimelineDetail(INITIATIVE_ID, OPERATION_ID,
          USER_ID);
    } catch (TimelineException e) {
      Assertions.fail();
    }
  }

  @Test
  void getTimelineDetail_not_found() {
    Mockito.when(timelineRepositoryMock.findByInitiativeIdAndOperationIdAndUserId(INITIATIVE_ID,
            OPERATION_ID, USER_ID))
        .thenReturn(Optional.empty());

    try {
      DetailOperationDTO actual = timelineService.getTimelineDetail(INITIATIVE_ID, OPERATION_ID,
          USER_ID);
      Assertions.fail();
    } catch (TimelineException e) {
      assertEquals(HttpStatus.NOT_FOUND.value(), e.getCode());
      assertEquals("Cannot find the requested operation!", e.getMessage());
    }
  }

  @Test
  void getTimeline_ok() {
    List<Operation> operations = new ArrayList<>();
    operations.add(OPERATION);
    Mockito.when(
            timelineRepositoryMock.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
        .thenReturn(new PageImpl<>(operations));
    TimelineDTO resDto = timelineService.getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, 0, 3);
    assertFalse(resDto.getOperationList().isEmpty());
    OperationDTO res = resDto.getOperationList().get(0);
    assertEquals(OPERATION.getOperationId(), res.getOperationId());
    assertEquals(OPERATION.getOperationType(), res.getOperationType());
    assertEquals(OPERATION.getHpan(), res.getHpan());
    assertEquals(OPERATION.getIban(), res.getIban());
    assertEquals(OPERATION.getCircuitType(), res.getCircuitType());
    assertEquals(OPERATION.getOperationDate().toString(), res.getOperationDate());
    assertEquals(OPERATION.getAmount().toString(), res.getAmount());
    assertEquals(OPERATION.getChannel(), res.getChannel());
  }

  @Test
  void getTimeline_ko() {
    Mockito.when(
            timelineRepositoryMock.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
        .thenReturn(new PageImpl<>(new ArrayList<>()));
    try {
      timelineService.getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, 0, 3);
    } catch (TimelineException e) {
      assertEquals(HttpStatus.NOT_FOUND.value(), e.getCode());
      assertEquals("No operations have been made on this initiative!", e.getMessage());
    }
  }

  @Test
  void sendToQueue() {
    Mockito.doNothing().when(timelineProducer).sendOperation(QUEUE_OPERATION_DTO);

    timelineService.sendToQueue(QUEUE_OPERATION_DTO);

    Mockito.verify(timelineProducer, Mockito.times(1))
        .sendOperation(QUEUE_OPERATION_DTO);
  }

  @Test
  void saveOperation(){

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    Mockito.verify(timelineRepositoryMock, Mockito.times(1))
        .save(Mockito.any());
  }
}
