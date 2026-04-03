package it.gov.pagopa.timeline.event.consumer;

import it.gov.pagopa.timeline.configuration.KafkaConsumerTracing;
import it.gov.pagopa.timeline.dto.QueueCommandOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class CommandsConsumer {

  @Bean
  public Consumer<QueueCommandOperationDTO> consumerCommands(
      TimelineService timelineService,
      KafkaConsumerTracing kafkaConsumerTracing,
      @Value("${spring.cloud.stream.bindings.consumerCommands-in-0.destination:}") String destination,
      @Value("${spring.cloud.stream.bindings.consumerCommands-in-0.group:}") String group) {
    return kafkaConsumerTracing.traceConsumer(
        "consumerCommands", destination, group, timelineService::processOperation);
  }
}
