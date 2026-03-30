package it.gov.pagopa.timeline.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EnableIntegrationMBeanExport.class)
@ConditionalOnBooleanProperty("spring.jmx.enabled")
public class NativeIntegrationJmxConfiguration {

  static final String MBEAN_EXPORTER_BEAN_NAME = "mbeanExporter";
  static final String INTEGRATION_MBEAN_EXPORTER_BEAN_NAME = "integrationMbeanExporter";

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  static BeanDefinitionRegistryPostProcessor nativeMBeanExporterBeanDefinitionRegistryPostProcessor() {
    return new NativeMBeanExporterBeanDefinitionRegistryPostProcessor(
        MBEAN_EXPORTER_BEAN_NAME,
        NativeAnnotationMBeanExporter.class);
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  static BeanDefinitionRegistryPostProcessor nativeIntegrationMBeanExporterBeanDefinitionRegistryPostProcessor() {
    return new NativeMBeanExporterBeanDefinitionRegistryPostProcessor(
        INTEGRATION_MBEAN_EXPORTER_BEAN_NAME,
        NativeIntegrationMBeanExporter.class);
  }
}
