package it.gov.pagopa.timeline.event.consumer;

import it.gov.pagopa.timeline.configuration.KafkaConsumerTracing;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class TimelineConsumer {

  @Bean
  public Consumer<Message<QueueOperationDTO>> consumerTimeline(
      TimelineService timelineService, KafkaConsumerTracing tracing) {
    return message ->
        tracing.traceConsumer(
            "consumerTimeline process", message, timelineService::saveOperation);
  }
}
