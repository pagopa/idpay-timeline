package it.gov.pagopa.timeline.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimelineDTO {

  private LocalDateTime lastUpdate;

  private List<OperationDTO> operationList;
  private int pageNo;
  private int pageSize;
  private int totalElements;
  private int totalPages;

}

