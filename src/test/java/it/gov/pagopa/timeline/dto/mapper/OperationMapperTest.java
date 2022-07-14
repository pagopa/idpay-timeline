package it.gov.pagopa.timeline.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.model.Operation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(
    value = {OperationMapper.class},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
class OperationMapperTest {

  private static final String OPERATION_ID = "test_operation";
  private static final String USER_ID = "test_user";
  private static final String INITIATIVE_ID = "test_initiative";
  private static final String OPERATION_TYPE = "PAID_REFUND";
  private static final String OPERATION_DATE = "2022-07-01T09:41:17.970";

  private static final QueueOperationDTO QUEUE_OPERATION_DTO = new QueueOperationDTO(OPERATION_ID,
      USER_ID, INITIATIVE_ID, OPERATION_TYPE, null, null, null, null, OPERATION_DATE, "0.00",
      "0.00", null, null);

  private static final QueueOperationDTO QUEUE_OPERATION_DTO_NO_BD = new QueueOperationDTO(OPERATION_ID,
      USER_ID, INITIATIVE_ID, OPERATION_TYPE, null, null, null, null, OPERATION_DATE, null,
      null, null, null);

  private static final Operation OPERATION = new Operation();
  private static final Operation OPERATION_NO_BD = new Operation();

  static{
    OPERATION.setOperationId(OPERATION_ID);
    OPERATION.setUserId(USER_ID);
    OPERATION.setInitiativeId(INITIATIVE_ID);
    OPERATION.setOperationType(OPERATION_TYPE);
    OPERATION.setOperationDate(LocalDateTime.parse(OPERATION_DATE));
    OPERATION.setAmount(new BigDecimal("0.00"));
    OPERATION.setAccrued(new BigDecimal("0.00"));

    OPERATION_NO_BD.setOperationId(OPERATION_ID);
    OPERATION_NO_BD.setUserId(USER_ID);
    OPERATION_NO_BD.setInitiativeId(INITIATIVE_ID);
    OPERATION_NO_BD.setOperationType(OPERATION_TYPE);
    OPERATION_NO_BD.setOperationDate(LocalDateTime.parse(OPERATION_DATE));
  }

  @Autowired
  OperationMapper operationMapper;

  @Test
  void map() {
    Operation actual = operationMapper.map(QUEUE_OPERATION_DTO);

    assertEquals(OPERATION, actual);
  }

  @Test
  void map_no_big_decimal() {
    Operation actual = operationMapper.map(QUEUE_OPERATION_DTO_NO_BD);

    assertEquals(OPERATION_NO_BD, actual);
  }
}
