package it.gov.pagopa.timeline.controller;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/idpay/timeline")
public interface TimelineController {

  /**
   * Returns the detail of an operation
   *
   * @param initiativeId
   * @param operationId
   * @param userId
   * @return
   */
  @GetMapping("/{initiativeId}/{operationId}/{userId}")
  ResponseEntity<DetailOperationDTO> getTimelineDetail(
      @PathVariable("initiativeId") String initiativeId,
      @PathVariable("operationId") String operationId,
      @PathVariable("userId") String userId);
}
