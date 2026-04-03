package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.spring.kafka.v2_7.SpringKafkaTelemetry;
import org.springframework.cloud.stream.binder.kafka.KafkaListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.BatchInterceptor;
import org.springframework.kafka.listener.RecordInterceptor;

@Configuration
public class KafkaTelemetryConfiguration {

  @Bean
  KafkaListenerContainerCustomizer otelKafkaListenerContainerCustomizer(
      OpenTelemetry openTelemetry) {
    SpringKafkaTelemetry springKafkaTelemetry = SpringKafkaTelemetry.create(openTelemetry);
    return (container, destinationName, group) ->
        configureInterceptors(container, springKafkaTelemetry);
  }

  private static <K, V> void configureInterceptors(
      AbstractMessageListenerContainer<K, V> container,
      SpringKafkaTelemetry springKafkaTelemetry) {
    RecordInterceptor<K, V> recordInterceptor = container.getRecordInterceptor();
    if (recordInterceptor == null) {
      container.setRecordInterceptor(springKafkaTelemetry.createRecordInterceptor());
    } else {
      container.setRecordInterceptor(
          springKafkaTelemetry.createRecordInterceptor(recordInterceptor));
    }

    BatchInterceptor<K, V> batchInterceptor = container.getBatchInterceptor();
    if (batchInterceptor == null) {
      container.setBatchInterceptor(springKafkaTelemetry.createBatchInterceptor());
    } else {
      container.setBatchInterceptor(
          springKafkaTelemetry.createBatchInterceptor(batchInterceptor));
    }
  }
}
