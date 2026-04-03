package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.stream.binder.kafka.config.ClientFactoryCustomizer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaTelemetryConfigurationTest {

  private final KafkaTelemetryConfiguration configuration = new KafkaTelemetryConfiguration();

  @Test
  void shouldWrapBinderManagedProducerFactories() {
    KafkaTelemetry kafkaTelemetry = configuration.kafkaTelemetry(OpenTelemetry.noop());
    ClientFactoryCustomizer customizer =
        configuration.otelKafkaClientFactoryCustomizer(kafkaTelemetry);
    DefaultKafkaProducerFactory<String, String> producerFactory =
        new DefaultKafkaProducerFactory<>(producerConfigs());

    customizer.configure(producerFactory);

    assertThat(producerFactory.getPostProcessors()).hasSize(1);
  }

  private static Map<String, Object> producerConfigs() {
    return Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
  }
}
