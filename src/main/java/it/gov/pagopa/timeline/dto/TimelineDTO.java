package it.gov.pagopa.timeline.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimelineDTO {

  private String lastUpdate;

  private List<OperationDTO> operationList;

}

