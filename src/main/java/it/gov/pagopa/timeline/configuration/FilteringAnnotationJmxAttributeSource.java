package it.gov.pagopa.timeline.configuration;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FilteringAnnotationJmxAttributeSource extends AnnotationJmxAttributeSource {

  private StringValueResolver valueResolver;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    super.setBeanFactory(beanFactory);
    if (beanFactory instanceof ConfigurableBeanFactory configurableBeanFactory) {
      this.valueResolver = new EmbeddedValueResolver(configurableBeanFactory);
    }
  }

  @Override
  public org.springframework.jmx.export.metadata.ManagedResource getManagedResource(Class<?> beanClass)
      throws InvalidMetadataException {
    MergedAnnotation<ManagedResource> annotation = MergedAnnotations
        .from(beanClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
        .get(ManagedResource.class)
        .withNonMergedAttributes();
    if (!annotation.isPresent()) {
      return null;
    }

    Class<?> sourceClass = (Class<?>) annotation.getSource();
    Class<?> metadataSource = sourceClass != null && !sourceClass.isInterface() ? sourceClass : beanClass;
    if (!Modifier.isPublic(metadataSource.getModifiers())) {
      throw new InvalidMetadataException(
          "Class '" + metadataSource.getName() + "' must be public to be exported as an MBean");
    }

    org.springframework.jmx.export.metadata.ManagedResource managedResource =
        new org.springframework.jmx.export.metadata.ManagedResource();
    List<PropertyValue> propertyValues = new ArrayList<>(annotation.asMap().size());
    annotation.asMap().forEach((name, value) -> {
      // Skip the optional cache metadata that still trips native-image JMX binding in this path.
      if (!"value".equals(name) && !"currencyTimeLimit".equals(name)) {
        Object resolvedValue = value;
        if (this.valueResolver != null && resolvedValue instanceof String stringValue) {
          resolvedValue = this.valueResolver.resolveStringValue(stringValue);
        }
        propertyValues.add(new PropertyValue(name, resolvedValue));
      }
    });
    PropertyAccessorFactory.forBeanPropertyAccess(managedResource)
        .setPropertyValues(new MutablePropertyValues(propertyValues));
    return managedResource;
  }
}
