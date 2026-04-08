package it.gov.pagopa.timeline.event.consumer;

import it.gov.pagopa.timeline.configuration.KafkaConsumerTracing;
import it.gov.pagopa.timeline.dto.QueueCommandOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class CommandsConsumer {

  @Bean
  public Consumer<Message<QueueCommandOperationDTO>> consumerCommands(
      TimelineService timelineService, KafkaConsumerTracing tracing) {
    return message ->
        tracing.traceConsumer(
            "consumerCommands process", message, timelineService::processOperation);
  }
}
