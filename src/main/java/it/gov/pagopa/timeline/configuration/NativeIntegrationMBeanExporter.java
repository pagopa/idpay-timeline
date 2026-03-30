package it.gov.pagopa.timeline.configuration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.jmx.JmxProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.util.StringUtils;

import javax.management.MBeanServer;

public class NativeIntegrationMBeanExporter extends IntegrationMBeanExporter {

  private ApplicationContext applicationContext;
  private final FilteringIntegrationJmxAttributeSource attributeSource = new FilteringIntegrationJmxAttributeSource();
  private final MetadataNamingStrategy namingStrategy = new MetadataNamingStrategy(this.attributeSource);

  public NativeIntegrationMBeanExporter() {
    setNamingStrategy(this.namingStrategy);
    setAssembler(new MetadataMBeanInfoAssembler(this.attributeSource));
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    super.setApplicationContext(applicationContext);
    this.applicationContext = applicationContext;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    super.setBeanFactory(beanFactory);
    this.attributeSource.setBeanFactory(beanFactory);
  }

  @Override
  public void setDefaultDomain(String defaultDomain) {
    super.setDefaultDomain(defaultDomain);
    this.namingStrategy.setDefaultDomain(defaultDomain);
  }

  @Override
  public void afterSingletonsInstantiated() {
    JmxProperties properties = this.applicationContext.getBean(JmxProperties.class);
    String defaultDomain = properties.getDefaultDomain();
    if (StringUtils.hasLength(defaultDomain)) {
      setDefaultDomain(defaultDomain);
    }
    MBeanServer server = this.applicationContext.getBean(properties.getServer(), MBeanServer.class);
    setServer(server);
    super.afterSingletonsInstantiated();
  }
}
