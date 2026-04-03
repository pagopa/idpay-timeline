package it.gov.pagopa.timeline.service;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import it.gov.pagopa.timeline.dto.QueueCommandOperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class TimelineServiceTracingTest {

  @Test
  void shouldMarkTimelineKafkaConsumersAsConsumerSpans() throws NoSuchMethodException {
    Method saveOperation = TimelineService.class.getMethod("saveOperation", QueueOperationDTO.class);
    Method processOperation =
        TimelineService.class.getMethod("processOperation", QueueCommandOperationDTO.class);

    assertThat(saveOperation.getAnnotation(WithSpan.class)).isNotNull();
    assertThat(saveOperation.getAnnotation(WithSpan.class).kind()).isEqualTo(SpanKind.CONSUMER);
    assertThat(saveOperation.getAnnotation(WithSpan.class).value())
        .isEqualTo("consumerTimeline process");

    assertThat(processOperation.getAnnotation(WithSpan.class)).isNotNull();
    assertThat(processOperation.getAnnotation(WithSpan.class).kind()).isEqualTo(SpanKind.CONSUMER);
    assertThat(processOperation.getAnnotation(WithSpan.class).value())
        .isEqualTo("consumerCommands process");
  }
}
