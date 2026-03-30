package it.gov.pagopa.timeline.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

class NativeMBeanExporterBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

  private final String beanName;
  private final Class<?> replacementType;

  NativeMBeanExporterBeanDefinitionRegistryPostProcessor(String beanName, Class<?> replacementType) {
    this.beanName = beanName;
    this.replacementType = replacementType;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    if (!registry.containsBeanDefinition(this.beanName)) {
      return;
    }

    BeanDefinition existingDefinition = registry.getBeanDefinition(this.beanName);
    RootBeanDefinition replacementDefinition = new RootBeanDefinition(this.replacementType);
    replacementDefinition.setRole(existingDefinition.getRole());
    replacementDefinition.setSource(existingDefinition.getSource());

    if (existingDefinition instanceof AbstractBeanDefinition abstractBeanDefinition) {
      replacementDefinition.setPrimary(abstractBeanDefinition.isPrimary());
      replacementDefinition.setLazyInit(abstractBeanDefinition.isLazyInit());
      replacementDefinition.setDependsOn(abstractBeanDefinition.getDependsOn());
      replacementDefinition.setSynthetic(abstractBeanDefinition.isSynthetic());
    }

    registry.removeBeanDefinition(this.beanName);
    registry.registerBeanDefinition(this.beanName, replacementDefinition);
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    // No-op
  }
}
