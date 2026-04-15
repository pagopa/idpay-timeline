package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerTracing {

  private final Tracer tracer;
  private final OpenTelemetry openTelemetry;

  public KafkaConsumerTracing(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
    this.tracer = openTelemetry.getTracer(KafkaConsumerTracing.class.getName());
  }

  public <T> void traceConsumer(String spanName, Message<T> message, Consumer<T> delegate) {
    Context parentContext =
        openTelemetry
            .getPropagators()
            .getTextMapPropagator()
            .extract(Context.current(), message.getHeaders(), HEADERS_GETTER);

    Span span =
        tracer
            .spanBuilder(spanName)
            .setParent(parentContext)
            .setSpanKind(SpanKind.CONSUMER)
            .setAttribute("messaging.system", "kafka")
            .setAttribute("messaging.operation", "process")
            .startSpan();

    try (Scope ignored = span.makeCurrent()) {
      delegate.accept(message.getPayload());
    } catch (RuntimeException | Error exception) {
      span.recordException(exception);
      span.setStatus(StatusCode.ERROR);
      throw exception;
    } finally {
      span.end();
    }
  }

  private static final TextMapGetter<MessageHeaders> HEADERS_GETTER =
      new TextMapGetter<>() {
        @Override
        public String get(MessageHeaders carrier, String key) {
          Object value = carrier.get(key);
          if (value instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
          }
          return value != null ? value.toString() : null;
        }

        @Override
        public Iterable<String> keys(MessageHeaders carrier) {
          return carrier.keySet();
        }
      };
}
