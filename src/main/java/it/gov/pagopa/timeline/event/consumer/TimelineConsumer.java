package it.gov.pagopa.timeline.event.consumer;

import it.gov.pagopa.timeline.configuration.KafkaConsumerTracing;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimelineConsumer {

  @Bean
  public Consumer<QueueOperationDTO> consumerTimeline(
      TimelineService timelineService,
      KafkaConsumerTracing kafkaConsumerTracing,
      @Value("${spring.cloud.stream.bindings.consumerTimeline-in-0.destination:}") String destination,
      @Value("${spring.cloud.stream.bindings.consumerTimeline-in-0.group:}") String group) {
    return kafkaConsumerTracing.traceConsumer(
        "consumerTimeline", destination, group, timelineService::saveOperation);
  }

}
