package it.gov.pagopa.timeline.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

class KafkaConsumerTracingTest {

  @Test
  void shouldCreateConsumerSpanWithContextExtraction() {
    var spanExporter = new CollectingSpanExporter();
    var tracerProvider =
        SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
            .build();
    var openTelemetry =
        OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

    try {
      var tracing = new KafkaConsumerTracing(openTelemetry);
      AtomicReference<String> received = new AtomicReference<>();
      Message<String> message = MessageBuilder.withPayload("test-payload").build();

      tracing.traceConsumer("consumerTimeline process", message, received::set);

      assertThat(received).hasValue("test-payload");
      assertThat(spanExporter.exportedSpans())
          .singleElement()
          .satisfies(
              span -> {
                assertThat(span.getName()).isEqualTo("consumerTimeline process");
                assertThat(span.getKind()).isEqualTo(SpanKind.CONSUMER);
                assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.system")))
                    .isEqualTo("kafka");
                assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.operation")))
                    .isEqualTo("process");
              });
    } finally {
      openTelemetry.close();
      tracerProvider.close();
    }
  }

  @Test
  void shouldRecordExceptionOnFailure() {
    var spanExporter = new CollectingSpanExporter();
    var tracerProvider =
        SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
            .build();
    var openTelemetry =
        OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

    try {
      var tracing = new KafkaConsumerTracing(openTelemetry);
      Message<String> message = MessageBuilder.withPayload("fail").build();

      assertThatThrownBy(
              () ->
                  tracing.traceConsumer(
                      "consumerTimeline process",
                      message,
                      payload -> {
                        throw new RuntimeException("processing failed");
                      }))
          .isInstanceOf(RuntimeException.class);

      assertThat(spanExporter.exportedSpans())
          .singleElement()
          .satisfies(
              span -> {
                assertThat(span.getStatus().getStatusCode()).isEqualTo(StatusCode.ERROR);
                assertThat(span.getEvents()).isNotEmpty();
              });
    } finally {
      openTelemetry.close();
      tracerProvider.close();
    }
  }

  @Test
  void shouldExtractParentContextFromHeaders() {
    var spanExporter = new CollectingSpanExporter();
    var tracerProvider =
        SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
            .build();
    var w3cPropagator = io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator.getInstance();
    var openTelemetry =
        OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .setPropagators(ContextPropagators.create(w3cPropagator))
            .build();

    try {
      var tracing = new KafkaConsumerTracing(openTelemetry);
      String traceId = "463ac35c9f6413ad48485a3953bb6124";
      String parentSpanId = "0020000000000001";
      Message<String> message =
          MessageBuilder.withPayload("payload")
              .copyHeaders(
                  Map.of(
                      "traceparent",
                      "00-" + traceId + "-" + parentSpanId + "-01"))
              .build();

      tracing.traceConsumer("test process", message, payload -> {});

      assertThat(spanExporter.exportedSpans())
          .singleElement()
          .satisfies(
              span -> {
                assertThat(span.getSpanContext().getTraceId()).isEqualTo(traceId);
                assertThat(span.getParentSpanId()).isEqualTo(parentSpanId);
              });
    } finally {
      openTelemetry.close();
      tracerProvider.close();
    }
  }

  private static final class CollectingSpanExporter implements SpanExporter {

    private final List<SpanData> spans = new CopyOnWriteArrayList<>();

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
      this.spans.addAll(new ArrayList<>(spans));
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
      return spans;
    }
  }
}
