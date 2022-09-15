package it.gov.pagopa.timeline.event.consumer;

import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimelineConsumer {

  @Bean
  public Consumer<QueueOperationDTO> consumerTimeline(TimelineService timelineService) {
    return timelineService::saveOperation;
  }

}
