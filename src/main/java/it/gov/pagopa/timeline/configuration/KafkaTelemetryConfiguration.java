package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.kafkaclients.v2_6.KafkaTelemetry;
import io.opentelemetry.instrumentation.spring.kafka.v2_7.SpringKafkaTelemetry;
import org.springframework.cloud.stream.binder.kafka.KafkaListenerContainerCustomizer;
import org.springframework.cloud.stream.binder.kafka.config.ClientFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;

@Configuration(proxyBeanMethods = false)
public class KafkaTelemetryConfiguration {

  @Bean
  SpringKafkaTelemetry springKafkaTelemetry(OpenTelemetry openTelemetry) {
    return SpringKafkaTelemetry.create(openTelemetry);
  }

  @Bean
  KafkaTelemetry kafkaTelemetry(OpenTelemetry openTelemetry) {
    return KafkaTelemetry.create(openTelemetry);
  }

  @Bean
  KafkaListenerContainerCustomizer otelKafkaListenerContainerCustomizer(
      SpringKafkaTelemetry springKafkaTelemetry) {
    return new KafkaListenerContainerCustomizer() {
      @Override
      public void configure(
          AbstractMessageListenerContainer<?, ?> container, String destinationName, String group) {
        instrumentContainer(container, springKafkaTelemetry);
      }
    };
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

  private static void instrumentContainer(
      AbstractMessageListenerContainer<?, ?> container, SpringKafkaTelemetry springKafkaTelemetry) {
    instrumentTypedContainer(container, springKafkaTelemetry);
  }

  private static <K, V> void instrumentTypedContainer(
      AbstractMessageListenerContainer<K, V> container, SpringKafkaTelemetry springKafkaTelemetry) {
    container.setRecordInterceptor(
        springKafkaTelemetry.createRecordInterceptor(container.getRecordInterceptor()));
    container.setBatchInterceptor(
        springKafkaTelemetry.createBatchInterceptor(container.getBatchInterceptor()));
  }
}
