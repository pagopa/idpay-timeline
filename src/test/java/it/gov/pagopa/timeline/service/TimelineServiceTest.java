package it.gov.pagopa.timeline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.OperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import it.gov.pagopa.timeline.dto.mapper.OperationMapper;
import it.gov.pagopa.timeline.event.producer.TimelineProducer;
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
import org.springframework.boot.test.mock.mockito.MockBean;
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
  private static final String EVENT_ID = "TEST_EVENT_ID";
  private static final String IBAN = "TEST_IBAN";
  private static final String CIRCUIT_TYPE = "00";
  private static final LocalDateTime OPERATION_DATE = LocalDateTime.now();
  private static final BigDecimal AMOUNT = new BigDecimal("50.00");
  private static final Operation OPERATION = new Operation();
  private static final String OPERATION_TYPE = "PAID_REFUND";
  private static final String CHANNEL = "APP_IO";
  private static final String INSTRUMENT_ID = "INSTRUMENT_ID";
  private static final String MASKED_PAN = "MASKED_PAN";
  private static final String BRAND_LOGO = "BAND_LOGO";


  private static final QueueOperationDTO QUEUE_OPERATION_DTO = new QueueOperationDTO(
      USER_ID, INITIATIVE_ID, OPERATION_TYPE, EVENT_ID, BRAND_LOGO, BRAND_LOGO, MASKED_PAN, INSTRUMENT_ID, null, null,
      null, null, null, null, null, null, null, null, null, null);
  private static final OperationDTO OPERATION_DTO = OperationDTO.builder().build();

  static {
    OPERATION.setOperationType(OPERATION_TYPE);
    OPERATION.setEventId(EVENT_ID);
    OPERATION.setInitiativeId(INITIATIVE_ID);
    OPERATION.setUserId(USER_ID);
    OPERATION.setMaskedPan(MASKED_PAN);
    OPERATION.setBrandLogo(BRAND_LOGO);
    OPERATION.setBrand(BRAND_LOGO);
    OPERATION.setInstrumentId(INSTRUMENT_ID);
    OPERATION.setIban(IBAN);
    OPERATION.setOperationDate(OPERATION_DATE);
    OPERATION.setAmount(AMOUNT);
    OPERATION.setCircuitType(CIRCUIT_TYPE);
    OPERATION.setChannel(CHANNEL);

    OPERATION_DTO.setOperationType(OPERATION_TYPE);
    OPERATION_DTO.setEventId(EVENT_ID);
    OPERATION_DTO.setBrandLogo(BRAND_LOGO);
    OPERATION_DTO.setBrand(BRAND_LOGO);
    OPERATION_DTO.setMaskedPan(MASKED_PAN);
    OPERATION_DTO.setInstrumentId(INSTRUMENT_ID);
    OPERATION_DTO.setIban(IBAN);
    OPERATION_DTO.setOperationDate(OPERATION_DATE);
    OPERATION_DTO.setAmount(AMOUNT);
    OPERATION_DTO.setCircuitType(CIRCUIT_TYPE);
    OPERATION_DTO.setChannel(CHANNEL);
  }

  @Test
  void getTimelineDetail_ok() {
    Mockito.when(timelineRepositoryMock.findByInitiativeIdAndOperationIdAndUserId(INITIATIVE_ID,
            OPERATION_ID, USER_ID))
        .thenReturn(Optional.of(OPERATION));

    try {
      DetailOperationDTO actual = timelineService.getTimelineDetail(INITIATIVE_ID, OPERATION_ID,
          USER_ID);
      assertNull(actual);
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
      timelineService.getTimelineDetail(INITIATIVE_ID, OPERATION_ID,
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

    Mockito.when(timelineRepositoryMock.findByFilter(Mockito.any(), Mockito.any())).thenReturn(operations);
    Mockito.when(operationMapper.toOperationDTO(Mockito.any(Operation.class))).thenReturn(OPERATION_DTO);

    TimelineDTO resDto = timelineService.getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, 1, 3,null,null);
    assertFalse(resDto.getOperationList().isEmpty());
    OperationDTO res = resDto.getOperationList().get(0);
    assertEquals(OPERATION.getOperationId(), res.getOperationId());
    assertEquals(OPERATION.getEventId(), res.getEventId());
    assertEquals(OPERATION.getOperationType(), res.getOperationType());
    assertEquals(OPERATION.getMaskedPan(), res.getMaskedPan());
    assertEquals(OPERATION.getBrandLogo(), res.getBrandLogo());
    assertEquals(OPERATION.getInstrumentId(), res.getInstrumentId());
    assertEquals(OPERATION.getIban(), res.getIban());
    assertEquals(OPERATION.getCircuitType(), res.getCircuitType());
    assertEquals(OPERATION.getOperationDate(), res.getOperationDate());
    assertEquals(OPERATION.getAmount(), res.getAmount());
    assertEquals(OPERATION.getChannel(), res.getChannel());
  }

  @Test
  void getTimeline_ko() {
    try {
      TimelineDTO resDto = timelineService.getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, 0, 3,null,null);
      assertNull(resDto.getLastUpdate());
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
  void saveOperation() {

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    Mockito.verify(timelineRepositoryMock, Mockito.times(1))
        .save(Mockito.any());
  }

  @Test
  void getRefunds_ok() {
    List<Operation> operations = new ArrayList<>();
    operations.add(OPERATION);
    Mockito.when(
            timelineRepositoryMock.findByInitiativeIdAndUserIdAndOperationTypeContainingOrderByOperationDateDesc(
                INITIATIVE_ID, USER_ID, "REFUND"))
        .thenReturn(operations);
    Mockito.when(operationMapper.toOperationDTO(Mockito.any(Operation.class)))
        .thenReturn(OPERATION_DTO);
    TimelineDTO resDto = timelineService.getRefunds(INITIATIVE_ID, USER_ID);
    assertFalse(resDto.getOperationList().isEmpty());
    OperationDTO res = resDto.getOperationList().get(0);
    assertEquals(OPERATION.getOperationId(), res.getOperationId());
    assertEquals(OPERATION.getEventId(), res.getEventId());
    assertEquals(OPERATION.getOperationType(), res.getOperationType());
    assertEquals(OPERATION.getMaskedPan(), res.getMaskedPan());
    assertEquals(OPERATION.getBrandLogo(), res.getBrandLogo());
    assertEquals(OPERATION.getInstrumentId(), res.getInstrumentId());
    assertEquals(OPERATION.getIban(), res.getIban());
    assertEquals(OPERATION.getCircuitType(), res.getCircuitType());
    assertEquals(OPERATION.getOperationDate(), res.getOperationDate());
    assertEquals(OPERATION.getAmount(), res.getAmount());
    assertEquals(OPERATION.getChannel(), res.getChannel());
  }

  @Test
  void getRefunds_ko() {
    Mockito.when(
            timelineRepositoryMock.findByInitiativeIdAndUserIdAndOperationTypeContainingOrderByOperationDateDesc(
                INITIATIVE_ID, USER_ID, "REFUND"))
        .thenReturn(new ArrayList<>());
    try {
      timelineService.getRefunds(INITIATIVE_ID, USER_ID);
    } catch (TimelineException e) {
      assertEquals(HttpStatus.NOT_FOUND.value(), e.getCode());
      assertEquals("No refunds have been rewarded on this initiative!", e.getMessage());
    }
  }
}
