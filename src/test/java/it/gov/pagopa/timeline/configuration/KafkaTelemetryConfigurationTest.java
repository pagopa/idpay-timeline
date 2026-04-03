package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.stream.binder.kafka.KafkaListenerContainerCustomizer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaTelemetryConfigurationTest {

  @Test
  void shouldConfigureOtelInterceptorsOnKafkaListenerContainers() {
    KafkaListenerContainerCustomizer customizer =
        new KafkaTelemetryConfiguration().otelKafkaListenerContainerCustomizer(OpenTelemetry.noop());
    ConcurrentMessageListenerContainer<String, String> container =
        new ConcurrentMessageListenerContainer<>(
            new DefaultKafkaConsumerFactory<>(consumerProperties()),
            new ContainerProperties("timeline-topic"));

    customizer.configure(container, "timeline-topic", "timeline-group");

    assertThat(container.getRecordInterceptor()).isNotNull();
    assertThat(container.getBatchInterceptor()).isNotNull();
  }

  private static Map<String, Object> consumerProperties() {
    return Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
        ConsumerConfig.GROUP_ID_CONFIG, "timeline-group",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
    );
  }
}
