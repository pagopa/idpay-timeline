package it.gov.pagopa.timeline.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.OperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.model.Operation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OperationMapper.class)
class OperationMapperTest {

  private static final String USER_ID = "test_user";
  private static final String INITIATIVE_ID = "test_initiative";
  private static final String OPERATION_TYPE = "PAID_REFUND";
  private static final String EVENT_ID = "EVENT_ID";
  private static final LocalDateTime OPERATION_DATE = LocalDateTime.now();
  private static final String INSTRUMENT_ID = "INSTRUMENT_ID";
  private static final String MASKED_PAN = "MASKED_PAN";
  private static final String BRAND_LOGO = "BRAND_LOGO";
  private static final String STATUS = "COMPLETED_OK";
  private static final String REFUND_TYPE = "ORDINARY";
  private static final String BUSINESS_NAME = "BUSINESS_NAME";
  private static final LocalDate START_DATE = LocalDate.now();
  private static final LocalDate END_DATE = LocalDate.now().plusDays(2);
  private static final LocalDate TRANSFER_DATE = LocalDate.now();
  private static final LocalDate NOTIFICATION_DATE = LocalDate.now();
  private static final String CRO = "CRO";
  private static final QueueOperationDTO QUEUE_OPERATION_DTO = new QueueOperationDTO(
      USER_ID, INITIATIVE_ID, OPERATION_TYPE, null, EVENT_ID, BRAND_LOGO, BRAND_LOGO, MASKED_PAN,
          INSTRUMENT_ID, null, null, null, CRO, null, OPERATION_DATE,
          new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), null, null,
          STATUS, REFUND_TYPE, START_DATE, END_DATE, TRANSFER_DATE, NOTIFICATION_DATE, BUSINESS_NAME);

  private static final Operation OPERATION = new Operation();
  private static final OperationDTO OPERATION_DTO = OperationDTO.builder().build();
  private static final DetailOperationDTO DETAIL_OPERATION_DTO = DetailOperationDTO.builder()
      .build();

  static {
    OPERATION.setUserId(USER_ID);
    OPERATION.setInitiativeId(INITIATIVE_ID);
    OPERATION.setOperationType(OPERATION_TYPE);
    OPERATION.setEventId(EVENT_ID);
    OPERATION.setOperationDate(OPERATION_DATE);
    OPERATION.setAmount(new BigDecimal("0.00"));
    OPERATION.setEffectiveAmount(new BigDecimal("0.00"));
    OPERATION.setAccrued(new BigDecimal("0.00"));
    OPERATION.setInstrumentId(INSTRUMENT_ID);
    OPERATION.setMaskedPan(MASKED_PAN);
    OPERATION.setBrandLogo(BRAND_LOGO);
    OPERATION.setBrand(BRAND_LOGO);
    OPERATION.setStatus(STATUS);
    OPERATION.setRefundType(REFUND_TYPE);
    OPERATION.setStartDate(START_DATE);
    OPERATION.setEndDate(END_DATE);
    OPERATION.setTransferDate(TRANSFER_DATE);
    OPERATION.setUserNotificationDate(NOTIFICATION_DATE);
    OPERATION.setCro(CRO);
    OPERATION.setBusinessName(BUSINESS_NAME);


    OPERATION_DTO.setOperationType(OPERATION_TYPE);
    OPERATION_DTO.setEventId(EVENT_ID);
    OPERATION_DTO.setOperationDate(OPERATION_DATE);
    OPERATION_DTO.setAmount(new BigDecimal("0.00"));
    OPERATION_DTO.setAccrued(new BigDecimal("0.00"));
    OPERATION_DTO.setMaskedPan(MASKED_PAN);
    OPERATION_DTO.setBrandLogo(BRAND_LOGO);
    OPERATION_DTO.setBrand(BRAND_LOGO);
    OPERATION_DTO.setInstrumentId(INSTRUMENT_ID);
    OPERATION_DTO.setStatus(STATUS);
    OPERATION_DTO.setBusinessName(BUSINESS_NAME);

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
    DETAIL_OPERATION_DTO.setCro(CRO);
    DETAIL_OPERATION_DTO.setBusinessName(BUSINESS_NAME);

  }

  @Autowired
  OperationMapper operationMapper;

  @Test
  void toOperation() {
    Operation actual = operationMapper.toOperation(QUEUE_OPERATION_DTO);

    assertEquals(OPERATION, actual);
  }

  @Test
  void toOperationDTO() {
    OperationDTO actual = operationMapper.toOperationDTO(OPERATION);

    assertEquals(OPERATION_DTO, actual);
  }

  @Test
  void toDetailOperationDTO() {
    DetailOperationDTO actual = operationMapper.toDetailOperationDTO(OPERATION);

    assertEquals(DETAIL_OPERATION_DTO, actual);
  }

}
