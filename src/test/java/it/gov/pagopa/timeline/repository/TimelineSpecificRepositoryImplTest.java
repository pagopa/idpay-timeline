package it.gov.pagopa.timeline.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.timeline.constants.TimelineConstants;
import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.model.Operation.Fields;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TimelineSpecificRepositoryImpl.class)
class TimelineSpecificRepositoryImplTest {

  @Autowired
  TimelineSpecificRepository timelineSpecificRepository;
  @MockBean
  MongoTemplate mongoTemplate;
  @MockBean
  Criteria criteria;

  private static final String USER_ID = "TEST_USER_ID";
  private static final String INITIATIVE_ID = "TEST_INITIATIVE_ID";
  private static final String OPERATION_TYPE = "TEST_OPERATION_TYPE";
  private static final LocalDateTime START_DATE = LocalDateTime.now();
  private static final LocalDateTime END_DATE = LocalDateTime.now();

  @Test
  void findByFilter() {
    Criteria criteria = new Criteria();
    Pageable paging = PageRequest.of(0, 20, Sort.by("lastUpdate"));

    timelineSpecificRepository.findByFilter(criteria, paging);
    verify(mongoTemplate, times(1)).find(Mockito.any(), Mockito.any());
  }

  @Test
  void findByFilter_criteria_null() {
    Criteria criteria = new Criteria();

    timelineSpecificRepository.findByFilter(criteria, null);
    verify(mongoTemplate, times(1)).find(Mockito.any(), Mockito.any());
  }

  @Test
  void getCount() {
    Criteria criteria = new Criteria();
    timelineSpecificRepository.getCount(criteria);
    verify(mongoTemplate, times(1)).count(Mockito.any(),
        (Class<?>) Mockito.any());
  }

  @Test
  void getCriteria() {
    Criteria criteria = timelineSpecificRepository.getCriteria(INITIATIVE_ID, USER_ID,
        OPERATION_TYPE, START_DATE, END_DATE);
    assertEquals(4, criteria.getCriteriaObject().size());
  }

  @Test
  void getCriteriaWithoutOperationType() {
    Criteria criteria = timelineSpecificRepository.getCriteria(INITIATIVE_ID, USER_ID, null,
        START_DATE, END_DATE);
    assertEquals(3, criteria.getCriteriaObject().size());
  }


  @Test
  void getCriteriaOnlyStartDate_Authorized() {
    Criteria criteria = timelineSpecificRepository.getCriteria(INITIATIVE_ID, USER_ID,
            TimelineConstants.TRX_STATUS_AUTHORIZED, START_DATE, null);
    assertEquals(4, criteria.getCriteriaObject().size());
  }

  @Test
  void getCriteriaOnlyEndDate_Cancelled() {
    Criteria criteria = timelineSpecificRepository.getCriteria(INITIATIVE_ID, USER_ID,
            TimelineConstants.TRX_STATUS_CANCELLED, null, END_DATE);
    assertEquals(4, criteria.getCriteriaObject().size());
  }

  @Test
  void getCriteriaWithoutDates() {
    Criteria criteria = timelineSpecificRepository.getCriteria(INITIATIVE_ID, USER_ID,
        OPERATION_TYPE, null, null);
    assertEquals(3, criteria.getCriteriaObject().size());
  }

  @Test
  void updateOperation() {
    String eventId = "123";
    String status = TimelineConstants.TRX_STATUS_REWARDED;

    timelineSpecificRepository.updateOperationStatusByEventId(eventId, status);

    verify(mongoTemplate, times(1)).updateFirst(
        Query.query(Criteria.where(Fields.eventId).is(eventId)),
        new Update().set(Fields.status, status),
        Operation.class);
  }
  @Test
  void deleteOperation() {
    String initiativeId = INITIATIVE_ID;

    timelineSpecificRepository.deleteOperation(initiativeId);

    verify(mongoTemplate, times(1)).findAndRemove(
            Query.query(Criteria.where(Fields.initiativeId).is(initiativeId)),
            Operation.class
    );
  }
}
