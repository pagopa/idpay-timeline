package it.gov.pagopa.timeline.controller;

import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import java.time.LocalDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/idpay/timeline")
public interface TimelineController {

  @GetMapping("/{initiativeId}/{operationId}/{userId}")
  ResponseEntity<DetailOperationDTO> getTimelineDetail(
      @PathVariable("initiativeId") String initiativeId,
      @PathVariable("operationId") String operationId, @PathVariable("userId") String userId);

  @GetMapping("/{initiativeId}/{userId}")
  ResponseEntity<TimelineDTO> getTimeline(@PathVariable("initiativeId") String initiativeId,
      @PathVariable("userId") String userId,
      @RequestParam(required = false) String operationType,
      @RequestParam(defaultValue = "0") @Min(value = 0, message = "Parameter [page] must be more than or equal to {value}") Integer page,
      @RequestParam(defaultValue = "3") @Min(value = 0, message = "Parameter [size] must be more than or equal to {value}") @Max(value = 10, message = "Parameter [size] must be less than or equal to {value}") Integer size,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo);

  @PutMapping("/")
  ResponseEntity<Void> addOperation(@Valid @RequestBody QueueOperationDTO body);

  @GetMapping("/{initiativeId}/{userId}/refunds")
  ResponseEntity<TimelineDTO> getRefunds(@PathVariable("initiativeId") String initiativeId,
      @PathVariable("userId") String userId);
}
