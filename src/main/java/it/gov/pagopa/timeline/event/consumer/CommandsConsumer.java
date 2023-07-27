package it.gov.pagopa.timeline.event.consumer;

import it.gov.pagopa.timeline.dto.QueueCommandOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class CommandsConsumer {
    @Bean
    public Consumer<QueueCommandOperationDTO> consumerCommands(TimelineService timelineService) {
        return timelineService::processOperation;
    }
}
