package it.gov.pagopa.timeline.configuration;

import it.gov.pagopa.common.logback.IgnoreCasePropertyEqualityCondition;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.jmx.export.metadata.AbstractJmxAttribute;
import org.springframework.jmx.export.metadata.ManagedAttribute;
import org.springframework.jmx.export.metadata.ManagedMetric;
import org.springframework.jmx.export.metadata.ManagedNotification;
import org.springframework.jmx.export.metadata.ManagedOperation;
import org.springframework.jmx.export.metadata.ManagedOperationParameter;
import org.springframework.jmx.export.metadata.ManagedResource;

public class NativeRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.reflection().registerType(
        IgnoreCasePropertyEqualityCondition.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_METHODS);
    hints.reflection().registerType(
        NativeIntegrationMBeanExporter.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
    hints.reflection().registerType(
        NativeAnnotationMBeanExporter.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
    hints.reflection().registerType(
        AbstractJmxAttribute.class,
        MemberCategory.INTROSPECT_PUBLIC_METHODS,
        MemberCategory.INVOKE_PUBLIC_METHODS);
    registerJmxMetadataType(hints, ManagedResource.class);
    registerJmxMetadataType(hints, ManagedAttribute.class);
    registerJmxMetadataType(hints, ManagedMetric.class);
    registerJmxMetadataType(hints, ManagedOperation.class);
    registerJmxMetadataType(hints, ManagedNotification.class);
    registerJmxMetadataType(hints, ManagedOperationParameter.class);
    hints.resources().registerPattern("org/joda/time/tz/data/**");
  }

  private static void registerJmxMetadataType(RuntimeHints hints, Class<?> type) {
    hints.reflection().registerType(
        type,
        MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.INTROSPECT_PUBLIC_METHODS,
        MemberCategory.INVOKE_PUBLIC_METHODS);
  }
}
