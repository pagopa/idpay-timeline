package it.gov.pagopa.timeline.service;

import it.gov.pagopa.timeline.constants.TimelineConstants;
import it.gov.pagopa.timeline.dto.*;
import it.gov.pagopa.timeline.dto.mapper.OperationMapper;
import it.gov.pagopa.timeline.event.producer.TimelineProducer;
import it.gov.pagopa.timeline.exception.custom.RefundsNotFoundException;
import it.gov.pagopa.timeline.exception.custom.TimelineDetailNotFoundException;
import it.gov.pagopa.timeline.exception.custom.UserNotFoundException;
import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.repository.TimelineRepository;
import it.gov.pagopa.timeline.utils.AuditUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TimelineServiceImpl.class)
@TestPropertySource(
        locations = "classpath:application.yml",
        properties = {
                "app.delete.paginationSize=100",
                "app.delete.delayTime=1000"
        })
class TimelineServiceTest {

  @MockBean
  TimelineRepository timelineRepositoryMock;

  @MockBean
  OperationMapper operationMapper;

  @Autowired
  TimelineService timelineService;

  @MockBean
  TimelineProducer timelineProducer;
  @MockBean
  AuditUtilities auditUtilities;

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
  private static final String OPERATION_TYPE_DELETE_INITIATIVE = "DELETE_INITIATIVE";
  private static final String CHANNEL = "APP_IO";
  private static final String INSTRUMENT_TYPE_QRCODE = "QRCODE";
  private static final String INSTRUMENT_ID = "INSTRUMENT_ID";
  private static final String MASKED_PAN = "MASKED_PAN";
  private static final String BRAND_LOGO = "BAND_LOGO";
  private static final String STATUS = "COMPLETED_OK";
  private static final String REFUND_TYPE = "ORDINARY";
  private static final String BUSINESS_NAME = "BUSINESS_NAME";
  private static final LocalDate START_DATE = LocalDate.now();
  private static final LocalDate END_DATE = LocalDate.now().plusDays(2);
  private static final LocalDate TRANSFER_DATE = LocalDate.now();
  private static final LocalDate NOTIFICATION_DATE = LocalDate.now();
  private static final String PAGINATION_VALUE = "100";


  private static final QueueOperationDTO QUEUE_OPERATION_DTO = new QueueOperationDTO(
      USER_ID, INITIATIVE_ID, OPERATION_TYPE, null, EVENT_ID, BRAND_LOGO, BRAND_LOGO, MASKED_PAN,
          INSTRUMENT_ID, null, null, CHANNEL, INSTRUMENT_TYPE_QRCODE, null, null, null,
          null, null, null, null, null, STATUS, REFUND_TYPE,
          START_DATE, END_DATE, TRANSFER_DATE, NOTIFICATION_DATE, BUSINESS_NAME);
  private static final OperationDTO OPERATION_DTO = OperationDTO.builder().build();
  private static final DetailOperationDTO DETAIL_OPERATION_DTO = DetailOperationDTO.builder()
          .build();

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
    OPERATION.setStatus(STATUS);
    OPERATION.setRefundType(REFUND_TYPE);
    OPERATION.setStartDate(START_DATE);
    OPERATION.setEndDate(END_DATE);
    OPERATION.setTransferDate(TRANSFER_DATE);
    OPERATION.setUserNotificationDate(NOTIFICATION_DATE);

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
    OPERATION_DTO.setStatus(STATUS);

    DETAIL_OPERATION_DTO.setOperationType(OPERATION_TYPE);
    DETAIL_OPERATION_DTO.setEventId(EVENT_ID);
    DETAIL_OPERATION_DTO.setOperationDate(OPERATION_DATE);
    DETAIL_OPERATION_DTO.setAmount(new BigDecimal("0.00"));
    DETAIL_OPERATION_DTO.setAccrued(new BigDecimal("0.00"));
    DETAIL_OPERATION_DTO.setMaskedPan(MASKED_PAN);
    DETAIL_OPERATION_DTO.setBrandLogo(BRAND_LOGO);
    DETAIL_OPERATION_DTO.setBrand(BRAND_LOGO);
    DETAIL_OPERATION_DTO.setInstrumentId(INSTRUMENT_ID);
    DETAIL_OPERATION_DTO.setStatus(STATUS);
    DETAIL_OPERATION_DTO.setRefundType(REFUND_TYPE);
    DETAIL_OPERATION_DTO.setStartDate(START_DATE);
    DETAIL_OPERATION_DTO.setEndDate(END_DATE);
    DETAIL_OPERATION_DTO.setTransferDate(TRANSFER_DATE);
    DETAIL_OPERATION_DTO.setUserNotificationDate(NOTIFICATION_DATE);
  }

  @Value("${app.delete.paginationSize}")
  private int pageSize;

  @AfterEach
  void tearDown() {
    Mockito.reset(timelineRepositoryMock, operationMapper, timelineProducer);
  }

  @Test
  void getTimelineDetail_ok() {
    Mockito.when(timelineRepositoryMock.findByInitiativeIdAndOperationIdAndUserId(INITIATIVE_ID,
            OPERATION_ID, USER_ID))
        .thenReturn(Optional.of(OPERATION));
    Mockito.when(operationMapper.toDetailOperationDTO(Mockito.any(Operation.class))).thenReturn(DETAIL_OPERATION_DTO);

    try {
      DetailOperationDTO actual = timelineService.getTimelineDetail(INITIATIVE_ID, OPERATION_ID,
          USER_ID);
      assertEquals(DETAIL_OPERATION_DTO, actual);
    } catch (TimelineDetailNotFoundException e) {
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
    } catch (TimelineDetailNotFoundException e) {
      assertEquals(TimelineConstants.TIMELINE_DETAIL_NOT_FOUND, e.getCode());
      assertEquals("Cannot find the detail of timeline on initiative [%s]".formatted(INITIATIVE_ID), e.getMessage());
    }
  }

  @Test
  void getTimeline_ok() {
    OPERATION.setStatus(STATUS);
    List<Operation> operations = new ArrayList<>();
    operations.add(OPERATION);

    Mockito.when(timelineRepositoryMock.findByFilter(Mockito.any(), Mockito.any())).thenReturn(operations);
    Mockito.when(operationMapper.toOperationDTO(Mockito.any(Operation.class))).thenReturn(OPERATION_DTO);
    Mockito.when(timelineRepositoryMock.findFirstByInitiativeIdAndUserIdOrderByOperationDateDesc(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Optional.of(OPERATION));

    TimelineDTO resDto = timelineService.getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, 1, 3,null,null);
    assertFalse(resDto.getOperationList().isEmpty());
    assertEquals(resDto.getLastUpdate(), OPERATION.getOperationDate());
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
    assertEquals(OPERATION.getStatus(), res.getStatus());
  }

  @Test
  void getTimelineEmpty(){
    List<Operation> operations = new ArrayList<>();

    Mockito.when(timelineRepositoryMock.findByFilter(Mockito.any(Criteria.class), Mockito.any(Pageable.class))).thenReturn(operations);

    try {
      timelineService.getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, 1, 3,null,null);
    }catch (UserNotFoundException e){
      assertEquals(TimelineConstants.TIMELINE_USER_NOT_FOUND, e.getCode());
      assertEquals("Timeline for the current user and initiative [%s] was not found".formatted(INITIATIVE_ID), e.getMessage());
    }
  }

  @Test
  void getTimelineWithNoFirstOperation_ok() {
    List<Operation> operations = new ArrayList<>();
    operations.add(OPERATION);

    Mockito.when(timelineRepositoryMock.findByFilter(Mockito.any(), Mockito.any())).thenReturn(operations);
    Mockito.when(operationMapper.toOperationDTO(Mockito.any(Operation.class))).thenReturn(OPERATION_DTO);

    TimelineDTO resDto = timelineService.getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, 1, 3, null, null);
    assertFalse(resDto.getOperationList().isEmpty());
    assertEquals(resDto.getLastUpdate(), OPERATION.getOperationDate());
  }

  @Test
  void sendToQueue() {
    Mockito.doNothing().when(timelineProducer).sendOperation(QUEUE_OPERATION_DTO);

    timelineService.sendToQueue(QUEUE_OPERATION_DTO);

    verify(timelineProducer, Mockito.times(1))
        .sendOperation(QUEUE_OPERATION_DTO);
  }
  @Test
  void getRefunds_ok() {
    OPERATION.setStatus(STATUS);
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
    assertEquals(OPERATION.getStatus(), res.getStatus());
  }

  @Test
  void getRefunds_ko() {
    Mockito.when(
            timelineRepositoryMock.findByInitiativeIdAndUserIdAndOperationTypeContainingOrderByOperationDateDesc(
                INITIATIVE_ID, USER_ID, "REFUND"))
        .thenReturn(new ArrayList<>());
    try {
      timelineService.getRefunds(INITIATIVE_ID, USER_ID);
    } catch (RefundsNotFoundException e) {
//      assertEquals(TimelineCostants.ExceptionCode.TIMELINE_INITIATIVE_NOT_REFUND, e.getCode());
      assertEquals("No refunds have been rewarded for the current user and initiative [%s]".formatted(INITIATIVE_ID), e.getMessage());
    }
  }

  @Test
  void saveOperation_APP_IO() {

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock, Mockito.times(1))
        .save(Mockito.any());
  }

  @Test
  void saveOperation_QRCODE_authorized() {
    QUEUE_OPERATION_DTO.setStatus(TimelineConstants.TRX_STATUS_AUTHORIZED);
    QUEUE_OPERATION_DTO.setChannel(TimelineConstants.CHANNEL_QRCODE);
    QUEUE_OPERATION_DTO.setOperationType(TimelineConstants.OPERATION_TYPE_TRX);

    Mockito.when(timelineRepositoryMock.findByEventId(EVENT_ID)).thenReturn(Optional.empty());

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock, Mockito.times(1))
        .save(Mockito.any());
  }

  @Test
  void saveOperation_QRCODE_rewarded() {
    QUEUE_OPERATION_DTO.setStatus(TimelineConstants.TRX_STATUS_REWARDED);
    QUEUE_OPERATION_DTO.setChannel(TimelineConstants.CHANNEL_QRCODE);

    Mockito.when(timelineRepositoryMock.findByEventId(EVENT_ID)).thenReturn(Optional.empty());

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock, Mockito.times(1))
        .save(Mockito.any());
  }

  @Test
  void saveOperation_QRCODE_operationRewarded() {
    OPERATION.setChannel(TimelineConstants.CHANNEL_QRCODE);
    OPERATION.setStatus(TimelineConstants.TRX_STATUS_REWARDED);
    QUEUE_OPERATION_DTO.setStatus(TimelineConstants.TRX_STATUS_AUTHORIZED);
    QUEUE_OPERATION_DTO.setChannel(TimelineConstants.CHANNEL_QRCODE);
    QUEUE_OPERATION_DTO.setOperationType(TimelineConstants.OPERATION_TYPE_TRX);

    Mockito.when(timelineRepositoryMock.findByEventId(EVENT_ID)).thenReturn(Optional.of(OPERATION));

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock).findByEventId(EVENT_ID);
    assertEquals(TimelineConstants.TRX_STATUS_REWARDED, OPERATION.getStatus());
  }

  @Test
  void saveOperation_QRCODE_imdepRewarded() {
    OPERATION.setChannel(TimelineConstants.CHANNEL_QRCODE);
    OPERATION.setStatus(TimelineConstants.TRX_STATUS_REWARDED);
    QUEUE_OPERATION_DTO.setStatus(TimelineConstants.TRX_STATUS_REWARDED);
    QUEUE_OPERATION_DTO.setChannel(TimelineConstants.CHANNEL_QRCODE);
    QUEUE_OPERATION_DTO.setOperationType(TimelineConstants.OPERATION_TYPE_TRX);

    Mockito.when(timelineRepositoryMock.findByEventId(EVENT_ID)).thenReturn(Optional.of(OPERATION));

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock).findByEventId(EVENT_ID);
    assertEquals(TimelineConstants.TRX_STATUS_REWARDED, OPERATION.getStatus());
  }

  @Test
  void saveOperation_QRCODE_imdepAuthorized() {
    OPERATION.setChannel(TimelineConstants.CHANNEL_QRCODE);
    OPERATION.setStatus(TimelineConstants.TRX_STATUS_AUTHORIZED);
    QUEUE_OPERATION_DTO.setStatus(TimelineConstants.TRX_STATUS_AUTHORIZED);
    QUEUE_OPERATION_DTO.setChannel(TimelineConstants.CHANNEL_QRCODE);
    QUEUE_OPERATION_DTO.setOperationType(TimelineConstants.OPERATION_TYPE_TRX);

    Mockito.when(timelineRepositoryMock.findByEventId(EVENT_ID)).thenReturn(Optional.of(OPERATION));

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock).findByEventId(EVENT_ID);
    assertEquals(TimelineConstants.TRX_STATUS_AUTHORIZED, OPERATION.getStatus());
  }
  @Test
  void saveOperation_QRCODE_channel_null() {
    QUEUE_OPERATION_DTO.setChannel(null);
    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock, Mockito.times(1))
        .save(Mockito.any());
  }
  @Test
  void saveOperation_QRCODE_channel_appio() {
    OPERATION.setChannel(TimelineConstants.CHANNEL_QRCODE);
    QUEUE_OPERATION_DTO.setStatus(TimelineConstants.TRX_STATUS_AUTHORIZED);
    QUEUE_OPERATION_DTO.setChannel(TimelineConstants.CHANNEL_QRCODE);
    QUEUE_OPERATION_DTO.setOperationType(TimelineConstants.OPERATION_TYPE_TRX);

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock, Mockito.times(1))
        .save(Mockito.any());
  }
  @Test
  void saveOperation_QRCODE_operationAuthorized() {
    OPERATION.setChannel(TimelineConstants.CHANNEL_QRCODE);
    OPERATION.setStatus(TimelineConstants.TRX_STATUS_AUTHORIZED);
    QUEUE_OPERATION_DTO.setStatus(TimelineConstants.TRX_STATUS_REWARDED);
    QUEUE_OPERATION_DTO.setChannel(TimelineConstants.CHANNEL_QRCODE);
    QUEUE_OPERATION_DTO.setOperationType(TimelineConstants.OPERATION_TYPE_TRX);

    Mockito.when(timelineRepositoryMock.findByEventId(EVENT_ID)).thenReturn(Optional.of(OPERATION));

    Mockito.doAnswer(invocationOnMock -> {
      OPERATION.setOperationDate(OPERATION_DATE);
      OPERATION.setStatus(TimelineConstants.TRX_STATUS_REWARDED);
      return OPERATION;
    }).when(timelineRepositoryMock).updateOperationStatusByEventId(EVENT_ID,TimelineConstants.TRX_STATUS_REWARDED);

    timelineService.saveOperation(QUEUE_OPERATION_DTO);

    verify(timelineRepositoryMock).findByEventId(EVENT_ID);
    assertEquals(TimelineConstants.TRX_STATUS_REWARDED, OPERATION.getStatus());
  }

  @ParameterizedTest
  @MethodSource("operationTypeAndInvocationTimes")
  void processOperation(String operationType, int times) {
    // Given
    final QueueCommandOperationDTO queueCommandOperationDTO = QueueCommandOperationDTO.builder()
            .entityId(INITIATIVE_ID)
            .operationType(operationType)
            .operationTime(LocalDateTime.now().minusMinutes(5))
            .build();
    Operation operation = new Operation();
    operation.setOperationId(OPERATION_ID);
    operation.setInitiativeId(INITIATIVE_ID);
    final List<Operation> deletedPage = List.of(operation);

    if(times == 2){
      final List<Operation> operationPage = createOperationPage(Integer.parseInt(PAGINATION_VALUE));
      when(timelineRepositoryMock.deletePaged(queueCommandOperationDTO.getEntityId(), pageSize))
              .thenReturn(operationPage)
              .thenReturn(deletedPage);
    } else {
      when(timelineRepositoryMock.deletePaged(queueCommandOperationDTO.getEntityId(), pageSize))
              .thenReturn(deletedPage);
    }

    // When
    if(times == 1){
      Thread.currentThread().interrupt();
    }
    timelineService.processOperation(queueCommandOperationDTO);

    // Then
    Mockito.verify(timelineRepositoryMock, Mockito.times(times)).deletePaged(queueCommandOperationDTO.getEntityId(), pageSize);
  }

  private static Stream<Arguments> operationTypeAndInvocationTimes() {
    return Stream.of(
            Arguments.of(OPERATION_TYPE_DELETE_INITIATIVE, 1),
            Arguments.of(OPERATION_TYPE_DELETE_INITIATIVE, 2),
            Arguments.of("OPERATION_TYPE_TEST", 0)
    );
  }

  private List<Operation> createOperationPage(int pageSize){
    List<Operation> operationPage = new ArrayList<>();

    for(int i=0;i<pageSize; i++){
      Operation operation = new Operation();
      operation.setOperationId(OPERATION_ID);
      operation.setInitiativeId(INITIATIVE_ID);
      operationPage.add(operation);
    }

    return operationPage;
  }
}
