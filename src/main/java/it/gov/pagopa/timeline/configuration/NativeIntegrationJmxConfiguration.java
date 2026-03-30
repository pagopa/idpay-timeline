package it.gov.pagopa.timeline.configuration;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

@Configuration(proxyBeanMethods = false)
public class NativeIntegrationJmxConfiguration {

  private static final String INTEGRATION_MBEAN_EXPORTER_BEAN_NAME = "integrationMbeanExporter";
  private static final Field ATTRIBUTE_SOURCE_FIELD = findField("attributeSource");
  private static final Field DEFAULT_NAMING_STRATEGY_FIELD = findField("defaultNamingStrategy");
  private static final Field DOMAIN_FIELD = findField("domain");

  @Bean
  BeanPostProcessor integrationMBeanExporterPostProcessor(ConfigurableBeanFactory beanFactory) {
    return new BeanPostProcessor() {
      @Override
      public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (INTEGRATION_MBEAN_EXPORTER_BEAN_NAME.equals(beanName)
            && bean instanceof IntegrationMBeanExporter exporter) {
          FilteringIntegrationJmxAttributeSource attributeSource = new FilteringIntegrationJmxAttributeSource();
          attributeSource.setBeanFactory(beanFactory);

          MetadataNamingStrategy namingStrategy = new MetadataNamingStrategy(attributeSource);
          String domain = (String) ReflectionUtils.getField(DOMAIN_FIELD, exporter);
          if (StringUtils.hasText(domain)) {
            namingStrategy.setDefaultDomain(domain);
          }

          // Spring Integration 7.0.2 still installs the legacy IntegrationJmxAttributeSource.
          // Swap it before startup so native images don't fail on currencyTimeLimit metadata.
          ReflectionUtils.setField(ATTRIBUTE_SOURCE_FIELD, exporter, attributeSource);
          ReflectionUtils.setField(DEFAULT_NAMING_STRATEGY_FIELD, exporter, namingStrategy);
          exporter.setNamingStrategy(namingStrategy);
          exporter.setAssembler(new MetadataMBeanInfoAssembler(attributeSource));
        }
        return bean;
      }
    };
  }

  private static Field findField(String fieldName) {
    Field field = ReflectionUtils.findField(IntegrationMBeanExporter.class, fieldName);
    if (field == null) {
      throw new IllegalStateException("Cannot find IntegrationMBeanExporter field '" + fieldName + "'");
    }
    ReflectionUtils.makeAccessible(field);
    return field;
  }
}
