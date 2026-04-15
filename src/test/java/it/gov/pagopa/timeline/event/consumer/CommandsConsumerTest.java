package it.gov.pagopa.timeline.event.consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.timeline.configuration.KafkaConsumerTracing;
import it.gov.pagopa.timeline.dto.QueueCommandOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

class CommandsConsumerTest {

  @Test
  void consumerCommandsShouldDelegateToTracing() {
    TimelineService timelineService = mock(TimelineService.class);
    KafkaConsumerTracing tracing = mock(KafkaConsumerTracing.class);

    Consumer<Message<QueueCommandOperationDTO>> consumer =
        new CommandsConsumer().consumerCommands(timelineService, tracing);

    QueueCommandOperationDTO payload = mock(QueueCommandOperationDTO.class);
    Message<QueueCommandOperationDTO> message = MessageBuilder.withPayload(payload).build();

    consumer.accept(message);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Consumer<QueueCommandOperationDTO>> delegateCaptor =
        ArgumentCaptor.forClass(Consumer.class);
    verify(tracing)
        .traceConsumer(
            org.mockito.ArgumentMatchers.eq("consumerCommands process"),
            org.mockito.ArgumentMatchers.same(message),
            delegateCaptor.capture());

    delegateCaptor.getValue().accept(payload);
    verify(timelineService).processOperation(payload);
  }
}
