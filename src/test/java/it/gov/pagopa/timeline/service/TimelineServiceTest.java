package it.gov.pagopa.timeline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.PutOperationDTO;
import it.gov.pagopa.timeline.event.TimelineProducer;
import it.gov.pagopa.timeline.exception.TimelineException;
import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.repository.TimelineRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = {TimelineService.class})
class TimelineServiceTest {

  @MockBean
  TimelineRepository timelineRepositoryMock;

  @MockBean
  TimelineProducer timelineProducer;

  @Autowired
  TimelineService timelineService;

  private static final String USER_ID = "TEST_USER_ID";
  private static final String INITIATIVE_ID = "TEST_INITIATIVE_ID";
  private static final String OPERATION_ID = "TEST_OPERATION_ID";
  private static final Operation OPERATION = new Operation();

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
}
