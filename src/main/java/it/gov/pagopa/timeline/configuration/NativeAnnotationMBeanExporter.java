package it.gov.pagopa.timeline.configuration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.jmx.JmxProperties;
import org.springframework.boot.autoconfigure.jmx.ParentAwareNamingStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.util.StringUtils;

import javax.management.MBeanServer;

public class NativeAnnotationMBeanExporter extends AnnotationMBeanExporter implements ApplicationContextAware {

  private ApplicationContext applicationContext;
  private final FilteringAnnotationJmxAttributeSource attributeSource = new FilteringAnnotationJmxAttributeSource();
  private final ParentAwareNamingStrategy namingStrategy = new ParentAwareNamingStrategy(this.attributeSource);

  public NativeAnnotationMBeanExporter() {
    setNamingStrategy(this.namingStrategy);
    setAssembler(new MetadataMBeanInfoAssembler(this.attributeSource));
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    this.namingStrategy.setApplicationContext(applicationContext);
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    super.setBeanFactory(beanFactory);
    this.attributeSource.setBeanFactory(beanFactory);
  }

  @Override
  public void afterSingletonsInstantiated() {
    JmxProperties properties = this.applicationContext.getBean(JmxProperties.class);
    setRegistrationPolicy(properties.getRegistrationPolicy());
    String defaultDomain = properties.getDefaultDomain();
    if (StringUtils.hasLength(defaultDomain)) {
      this.namingStrategy.setDefaultDomain(defaultDomain);
    }
    this.namingStrategy.setEnsureUniqueRuntimeObjectNames(properties.isUniqueNames());
    String serverBeanName = properties.getServer();
    if (StringUtils.hasLength(serverBeanName)) {
      MBeanServer server = this.applicationContext.getBean(serverBeanName, MBeanServer.class);
      setServer(server);
    }
    setEnsureUniqueRuntimeObjectNames(properties.isUniqueNames());
    super.afterSingletonsInstantiated();
  }
}
