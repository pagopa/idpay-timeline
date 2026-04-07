package it.gov.pagopa.timeline.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.observation.ObservationRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.stream.binder.kafka.config.ClientFactoryCustomizer;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

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

  @Test
  void shouldEnableObservationOnListenerContainers() {
    ObservationRegistry observationRegistry = ObservationRegistry.create();
    ListenerContainerCustomizer<AbstractMessageListenerContainer<?, ?>> customizer =
        configuration.otelListenerContainerCustomizer(observationRegistry);

    DefaultKafkaConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<>(consumerConfigs());
    ContainerProperties containerProperties = new ContainerProperties("test-topic");
    KafkaMessageListenerContainer<String, String> container =
        new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

    customizer.configure(container, "test-topic", "test-group");

    assertThat(container.getContainerProperties().isObservationEnabled()).isTrue();
    assertThat(container.getContainerProperties().getObservationRegistry())
        .isSameAs(observationRegistry);
  }

  private static Map<String, Object> producerConfigs() {
    return Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
  }

  private static Map<String, Object> consumerConfigs() {
    return Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
        ConsumerConfig.GROUP_ID_CONFIG, "timeline-group",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
  }
}
