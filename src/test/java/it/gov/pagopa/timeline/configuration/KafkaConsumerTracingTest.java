package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConsumerTracingTest {

  @Test
  void shouldCreateKafkaConsumerProcessSpan() {
    CollectingSpanExporter spanExporter = new CollectingSpanExporter();
    SdkTracerProvider tracerProvider =
        SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
            .build();
    OpenTelemetrySdk openTelemetry =
        OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

    try {
      KafkaConsumerTracing kafkaConsumerTracing = new KafkaConsumerTracing(openTelemetry);
      AtomicBoolean invoked = new AtomicBoolean();
      Consumer<String> tracedConsumer =
          kafkaConsumerTracing.traceConsumer(
              "consumerTimeline", "timeline-topic", "timeline-group", payload -> invoked.set(true));

      tracedConsumer.accept("payload");

      assertThat(invoked).isTrue();
      assertThat(spanExporter.exportedSpans())
          .singleElement()
          .satisfies(span -> {
            assertThat(span.getName()).isEqualTo("consumerTimeline process");
            assertThat(span.getKind()).isEqualTo(SpanKind.CONSUMER);
            assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.system")))
                .isEqualTo("kafka");
            assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.operation")))
                .isEqualTo("process");
            assertThat(
                    span.getAttributes().get(AttributeKey.stringKey("messaging.destination.name")))
                .isEqualTo("timeline-topic");
            assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.destination")))
                .isEqualTo("timeline-topic");
            assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.source.name")))
                .isEqualTo("timeline-topic");
            assertThat(
                    span.getAttributes()
                        .get(AttributeKey.stringKey("messaging.kafka.consumer.group")))
                .isEqualTo("timeline-group");
          });
    } finally {
      openTelemetry.close();
      tracerProvider.close();
    }
  }

  private static final class CollectingSpanExporter implements SpanExporter {

    private final List<SpanData> exportedSpans = new CopyOnWriteArrayList<>();

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
      exportedSpans.addAll(new ArrayList<>(spans));
      return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
      return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
      return CompletableResultCode.ofSuccess();
    }

    List<SpanData> exportedSpans() {
      return exportedSpans;
    }
  }
}
