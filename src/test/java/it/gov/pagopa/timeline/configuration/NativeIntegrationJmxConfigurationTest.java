package it.gov.pagopa.timeline.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.integration.autoconfigure.IntegrationAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.management.IntegrationManagedResource;
import org.springframework.integration.monitor.IntegrationMBeanExporter;

import static org.assertj.core.api.Assertions.assertThat;

class NativeIntegrationJmxConfigurationTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(JmxAutoConfiguration.class, IntegrationAutoConfiguration.class))
      .withUserConfiguration(NativeIntegrationJmxConfiguration.class);

  private final ApplicationContextRunner managedResourceContextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(JmxAutoConfiguration.class))
      .withUserConfiguration(NativeIntegrationJmxConfiguration.class, ManagedResourceTestConfiguration.class);

  @Test
  void shouldRegisterNativeSafeIntegrationMBeanExporter() {
    this.contextRunner
        .withPropertyValues("spring.jmx.enabled=true")
        .run(context -> {
          assertThat(context).hasSingleBean(IntegrationMBeanExporter.class);
          assertThat(context.getBean(NativeIntegrationJmxConfiguration.MBEAN_EXPORTER_BEAN_NAME))
              .isInstanceOf(NativeAnnotationMBeanExporter.class);
          assertThat(context.getBean(NativeIntegrationJmxConfiguration.INTEGRATION_MBEAN_EXPORTER_BEAN_NAME))
              .isInstanceOf(NativeIntegrationMBeanExporter.class);
        });
  }

  @Test
  void shouldStartBootJmxExporterForIntegrationManagedResource() {
    this.managedResourceContextRunner
        .withPropertyValues("spring.jmx.enabled=true")
        .run(context -> assertThat(context.getBean(NativeIntegrationJmxConfiguration.MBEAN_EXPORTER_BEAN_NAME))
            .isInstanceOf(NativeAnnotationMBeanExporter.class));
  }

  @Test
  void shouldNotRegisterExporterWhenJmxIsDisabled() {
    this.contextRunner
        .withPropertyValues("spring.jmx.enabled=false")
        .run(context -> assertThat(context).doesNotHaveBean(IntegrationMBeanExporter.class));
  }

  @Configuration(proxyBeanMethods = false)
  static class ManagedResourceTestConfiguration {

    @Bean
    SampleManagedResource sampleManagedResource() {
      return new SampleManagedResource();
    }
  }

  @IntegrationManagedResource(objectName = "bean:name=test", currencyTimeLimit = 30)
  public static class SampleManagedResource {
  }
}
