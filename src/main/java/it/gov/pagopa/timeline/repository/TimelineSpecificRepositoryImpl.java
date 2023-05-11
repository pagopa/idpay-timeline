package it.gov.pagopa.timeline.repository;


import it.gov.pagopa.timeline.model.Operation;
import it.gov.pagopa.timeline.model.Operation.Fields;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class TimelineSpecificRepositoryImpl implements TimelineSpecificRepository {

  private final MongoTemplate mongoTemplate;

  public TimelineSpecificRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

@Override
  public List<Operation> findByFilter(Criteria criteria, Pageable pageable) {
    return mongoTemplate.find(
        Query.query(criteria)
            .with(this.getPageable(pageable)),
        Operation.class);
  }

  @Override
  public long getCount(Criteria criteria){
    Query query = new Query();
    query.addCriteria(criteria);
    return mongoTemplate.count(query, Operation.class);
  }

  @Override
  public void updateOperationStatusByTransactionId(String transactionId,String status) {
    mongoTemplate.updateFirst(
        Query.query(Criteria.where(Fields.transactionId).is(transactionId)),
        new Update()
            .set(Fields.status, status),
        Operation.class);
  }

  public Criteria getCriteria(String initiativeId, String userId, String operationType,
      LocalDateTime startDate, LocalDateTime endDate) {

    Criteria criteria = Criteria.where(Operation.Fields.initiativeId).is(initiativeId);
    criteria.and(Operation.Fields.userId).is(userId);
    if (operationType != null) {
      criteria.and(Fields.operationType).is(operationType);
    }
    if (startDate != null && endDate != null) {
      criteria.and(Fields.operationDate)
          .gte(startDate)
          .lte(endDate);
    } else if (startDate != null) {
      criteria.and(Fields.operationDate)
          .gte(startDate);
    } else if (endDate != null) {
      criteria.and(Fields.operationDate)
          .lte(endDate);
    }
    return criteria;
  }


  private Pageable getPageable(Pageable pageable) {
    if (pageable == null) {
      return PageRequest.of(0, 10, Sort.by("operationDate"));
    }
    return pageable;
  }
}
