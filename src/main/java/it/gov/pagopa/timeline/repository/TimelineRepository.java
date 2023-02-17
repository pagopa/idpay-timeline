package it.gov.pagopa.timeline.repository;

import it.gov.pagopa.timeline.model.Operation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineRepository extends MongoRepository<Operation, String>, TimelineSpecificRepository {

  Optional<Operation> findByInitiativeIdAndOperationIdAndUserId(String initiativeId,
      String operationId, String userId);

  List<Operation> findByInitiativeIdAndUserIdAndOperationTypeContainingOrderByOperationDateDesc(
      String initiativeId, String userId, String operationType);

  Optional<Operation> findFirstByInitiativeIdAndUserIdOrderByOperationDateDesc(String initiativeId, String userId);

  Criteria getCriteria(String initiativeId, String userId, String operationType,
          LocalDateTime startDate, LocalDateTime endDate);
}
