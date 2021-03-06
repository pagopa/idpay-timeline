package it.gov.pagopa.timeline.repository;

import it.gov.pagopa.timeline.model.Operation;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineRepository extends MongoRepository<Operation, String> {
  Optional<Operation> findByInitiativeIdAndOperationIdAndUserId(String initiativeId, String operationId, String userId);
}
