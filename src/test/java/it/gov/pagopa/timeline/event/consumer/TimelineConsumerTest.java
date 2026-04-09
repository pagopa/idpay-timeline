package it.gov.pagopa.timeline.event.consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.timeline.configuration.KafkaConsumerTracing;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

class TimelineConsumerTest {

  @Test
  void consumerTimelineShouldDelegateToTracing() {
    TimelineService timelineService = mock(TimelineService.class);
    KafkaConsumerTracing tracing = mock(KafkaConsumerTracing.class);

    Consumer<Message<QueueOperationDTO>> consumer =
        new TimelineConsumer().consumerTimeline(timelineService, tracing);

    QueueOperationDTO payload = mock(QueueOperationDTO.class);
    Message<QueueOperationDTO> message = MessageBuilder.withPayload(payload).build();

    consumer.accept(message);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Consumer<QueueOperationDTO>> delegateCaptor =
        ArgumentCaptor.forClass(Consumer.class);
    verify(tracing)
        .traceConsumer(
            org.mockito.ArgumentMatchers.eq("consumerTimeline process"),
            org.mockito.ArgumentMatchers.same(message),
            delegateCaptor.capture());

    delegateCaptor.getValue().accept(payload);
    verify(timelineService).saveOperation(payload);
  }
}
