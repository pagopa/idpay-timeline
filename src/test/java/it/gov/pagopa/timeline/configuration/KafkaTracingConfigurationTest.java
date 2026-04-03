package it.gov.pagopa.timeline.configuration;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import it.gov.pagopa.timeline.dto.QueueCommandOperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.service.TimelineService;
import it.gov.pagopa.timeline.service.TimelineServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.lang.reflect.Method;
import java.util.Properties;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class KafkaTracingConfigurationTest {

  @Test
  void shouldPreferBinderObservationOverManualServiceSpans() throws NoSuchMethodException {
    assertManualInstrumentationIsAbsent(
        TimelineService.class.getMethod("saveOperation", QueueOperationDTO.class));
    assertManualInstrumentationIsAbsent(
        TimelineService.class.getMethod("processOperation", QueueCommandOperationDTO.class));
    assertManualInstrumentationIsAbsent(
        TimelineServiceImpl.class.getMethod("saveOperation", QueueOperationDTO.class));
    assertManualInstrumentationIsAbsent(
        TimelineServiceImpl.class.getMethod("processOperation", QueueCommandOperationDTO.class));

    Properties applicationProperties = loadApplicationProperties();

    assertThat(applicationProperties)
        .containsEntry("spring.cloud.stream.kafka.binder.enable-observation", true)
        .containsEntry(
            "spring.cloud.stream.binders.kafkaIn.environment.spring.cloud.stream.kafka.binder.enable-observation",
            true)
        .containsEntry(
            "spring.cloud.stream.binders.kafkaOut.environment.spring.cloud.stream.kafka.binder.enable-observation",
            true)
        .containsEntry(
            "spring.cloud.stream.binders.kafkaCommands.environment.spring.cloud.stream.kafka.binder.enable-observation",
            true);
  }

  private static void assertManualInstrumentationIsAbsent(Method method) {
    assertThat(method.getAnnotation(WithSpan.class)).isNull();
  }

  private static Properties loadApplicationProperties() {
    YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
    yamlPropertiesFactoryBean.setResources(new ClassPathResource("application.yml"));
    return requireNonNull(yamlPropertiesFactoryBean.getObject());
  }
}
