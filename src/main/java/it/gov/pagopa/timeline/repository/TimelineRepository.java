package it.gov.pagopa.timeline.repository;

import it.gov.pagopa.timeline.model.Operation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineRepository extends MongoRepository<Operation, String> {
  Optional<Operation> findByInitiativeIdAndOperationIdAndUserId(String initiativeId, String operationId, String userId);
  List<Operation> findByInitiativeIdAndUserIdOrderByOperationDateDesc(String initiativeId, String userId);
}
