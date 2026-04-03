package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;
import io.opentelemetry.instrumentation.spring.kafka.v2_7.SpringKafkaTelemetry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.kafka.KafkaListenerContainerCustomizer;
import org.springframework.cloud.stream.binder.kafka.config.ClientFactoryCustomizer;
import org.springframework.cloud.stream.binder.kafka.properties.KafkaConsumerProperties;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaTelemetryConfigurationTest {

  private final KafkaTelemetryConfiguration configuration = new KafkaTelemetryConfiguration();

  @Test
  void shouldAttachOpenTelemetryInterceptorsToBinderManagedListenerContainers() {
    SpringKafkaTelemetry springKafkaTelemetry =
        configuration.springKafkaTelemetry(OpenTelemetry.noop());
    KafkaListenerContainerCustomizer customizer =
        configuration.otelKafkaListenerContainerCustomizer(springKafkaTelemetry);
    ConcurrentMessageListenerContainer<String, String> container =
        new ConcurrentMessageListenerContainer<>(
            new DefaultKafkaConsumerFactory<>(consumerConfigs()),
            new ContainerProperties("topic"));

    customizer.configure(
        container, "topic", "group", new ExtendedConsumerProperties<>(new KafkaConsumerProperties()));

    assertThat(container.getRecordInterceptor()).isNotNull();
    assertThat(container.getBatchInterceptor()).isNotNull();
  }

  @Test
  void shouldWrapBinderManagedProducerAndConsumerFactories() {
    KafkaTelemetry kafkaTelemetry = configuration.kafkaTelemetry(OpenTelemetry.noop());
    ClientFactoryCustomizer customizer =
        configuration.otelKafkaClientFactoryCustomizer(kafkaTelemetry);
    DefaultKafkaProducerFactory<String, String> producerFactory =
        new DefaultKafkaProducerFactory<>(producerConfigs());
    DefaultKafkaConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<>(consumerConfigs());

    customizer.configure(producerFactory);
    customizer.configure(consumerFactory);

    assertThat(producerFactory.getPostProcessors()).hasSize(1);
    assertThat(consumerFactory.getPostProcessors()).hasSize(1);
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
        ConsumerConfig.GROUP_ID_CONFIG, "group",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
  }
}
