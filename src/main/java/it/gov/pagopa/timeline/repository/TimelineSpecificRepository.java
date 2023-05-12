package it.gov.pagopa.timeline.repository;

import it.gov.pagopa.timeline.model.Operation;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;


public interface TimelineSpecificRepository {
  List<Operation> findByFilter(Criteria criteria, Pageable pageable);
  Criteria getCriteria(String initiativeId, String userId, String operationType,
      LocalDateTime startDate, LocalDateTime endDate);
  long getCount(Criteria criteria);

  void updateOperationStatusByEventId(String eventId, String status);
}
