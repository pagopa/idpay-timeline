package it.gov.pagopa.timeline.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimelineDTO {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private LocalDateTime lastUpdate;
  private List<OperationDTO> operationList;
  private int pageNo;
  private int pageSize;
  private int totalElements;
  private int totalPages;

}

