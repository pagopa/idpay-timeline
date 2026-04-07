package it.gov.pagopa.timeline.configuration;

import io.micrometer.observation.ObservationRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;
import org.springframework.cloud.stream.binder.kafka.config.ClientFactoryCustomizer;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;

@Configuration(proxyBeanMethods = false)
public class KafkaTelemetryConfiguration {

  @Bean
  KafkaTelemetry kafkaTelemetry(OpenTelemetry openTelemetry) {
    return KafkaTelemetry.create(openTelemetry);
  }

  @Bean
  ClientFactoryCustomizer otelKafkaClientFactoryCustomizer(KafkaTelemetry kafkaTelemetry) {
    return new ClientFactoryCustomizer() {
      @Override
      public void configure(ProducerFactory<?, ?> producerFactory) {
        if (producerFactory instanceof DefaultKafkaProducerFactory<?, ?> defaultProducerFactory) {
          defaultProducerFactory.addPostProcessor(kafkaTelemetry::wrap);
        }
      }
    };
  }

  @Bean
  ListenerContainerCustomizer<AbstractMessageListenerContainer<?, ?>> otelListenerContainerCustomizer(
      ObservationRegistry observationRegistry) {
    return (container, destinationName, group) -> {
      container.getContainerProperties().setObservationEnabled(true);
      container.getContainerProperties().setObservationRegistry(observationRegistry);
    };
  }
}
