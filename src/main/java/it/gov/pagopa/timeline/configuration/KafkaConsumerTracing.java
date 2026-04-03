package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

@Component
public class KafkaConsumerTracing {

  private static final AttributeKey<String> MESSAGING_SYSTEM =
      AttributeKey.stringKey("messaging.system");
  private static final AttributeKey<String> MESSAGING_OPERATION =
      AttributeKey.stringKey("messaging.operation");
  private static final AttributeKey<String> MESSAGING_DESTINATION_NAME =
      AttributeKey.stringKey("messaging.destination.name");
  private static final AttributeKey<String> MESSAGING_DESTINATION =
      AttributeKey.stringKey("messaging.destination");
  private static final AttributeKey<String> MESSAGING_DESTINATION_KIND =
      AttributeKey.stringKey("messaging.destination.kind");
  private static final AttributeKey<String> MESSAGING_SOURCE_NAME =
      AttributeKey.stringKey("messaging.source.name");
  private static final AttributeKey<String> MESSAGING_SOURCE_KIND =
      AttributeKey.stringKey("messaging.source.kind");
  private static final AttributeKey<String> MESSAGING_KAFKA_CONSUMER_GROUP =
      AttributeKey.stringKey("messaging.kafka.consumer.group");

  private final Tracer tracer;

  public KafkaConsumerTracing(OpenTelemetry openTelemetry) {
    this.tracer = openTelemetry.getTracer(KafkaConsumerTracing.class.getName());
  }

  public <T> Consumer<T> traceConsumer(
      String consumerName, String destinationName, String consumerGroup, Consumer<T> delegate) {
    return payload -> {
      var spanBuilder = tracer.spanBuilder(consumerName + " process")
          .setSpanKind(SpanKind.CONSUMER)
          .setAttribute(MESSAGING_SYSTEM, "kafka")
          .setAttribute(MESSAGING_OPERATION, "process");

      if (StringUtils.hasText(destinationName)) {
        spanBuilder
            .setAttribute(MESSAGING_DESTINATION_NAME, destinationName)
            .setAttribute(MESSAGING_DESTINATION, destinationName)
            .setAttribute(MESSAGING_DESTINATION_KIND, "topic")
            .setAttribute(MESSAGING_SOURCE_NAME, destinationName)
            .setAttribute(MESSAGING_SOURCE_KIND, "topic");
      }
      if (StringUtils.hasText(consumerGroup)) {
        spanBuilder.setAttribute(MESSAGING_KAFKA_CONSUMER_GROUP, consumerGroup);
      }

      Span span = spanBuilder.startSpan();
      try (Scope ignored = span.makeCurrent()) {
        delegate.accept(payload);
      } catch (RuntimeException | Error exception) {
        span.recordException(exception);
        span.setStatus(StatusCode.ERROR);
        throw exception;
      } finally {
        span.end();
      }
    };
  }
}
