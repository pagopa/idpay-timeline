package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;
import org.springframework.cloud.stream.binder.kafka.config.ClientFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

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

      @Override
      public void configure(ConsumerFactory<?, ?> consumerFactory) {
        if (consumerFactory instanceof DefaultKafkaConsumerFactory<?, ?> defaultConsumerFactory) {
          defaultConsumerFactory.addPostProcessor(kafkaTelemetry::wrap);
        }
      }
    };
  }
}
