package it.gov.pagopa.timeline.configuration;

import it.gov.pagopa.common.logback.IgnoreCasePropertyEqualityCondition;
import org.springframework.aot.hint.ExecutableMode;
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

import javax.security.auth.Subject;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;

public class NativeRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.reflection().registerType(
        IgnoreCasePropertyEqualityCondition.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_METHODS);
    hints.reflection().registerType(
        AbstractJmxAttribute.class,
        MemberCategory.INVOKE_PUBLIC_METHODS);
    registerJmxMetadataType(hints, ManagedResource.class);
    registerJmxMetadataType(hints, ManagedAttribute.class);
    registerJmxMetadataType(hints, ManagedMetric.class);
    registerJmxMetadataType(hints, ManagedOperation.class);
    registerJmxMetadataType(hints, ManagedNotification.class);
    registerJmxMetadataType(hints, ManagedOperationParameter.class);
    registerKafkaSaslCompatibilityHints(hints);
    hints.resources().registerPattern("org/joda/time/tz/data/**");
  }

  private static void registerJmxMetadataType(RuntimeHints hints, Class<?> type) {
    hints.reflection().registerType(
        type,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_METHODS);
  }

  private static void registerKafkaSaslCompatibilityHints(RuntimeHints hints) {
    hints.reflection().registerType(AccessController.class);
    hints.reflection().registerType(AccessControlContext.class);
    hints.reflection().registerType(Subject.class);

    hints.reflection().registerMethod(
        method(AccessController.class, "doPrivileged", java.security.PrivilegedAction.class),
        ExecutableMode.INVOKE);
    hints.reflection().registerMethod(method(AccessController.class, "getContext"), ExecutableMode.INVOKE);
    hints.reflection().registerMethod(method(Subject.class, "getSubject", AccessControlContext.class), ExecutableMode.INVOKE);
    hints.reflection().registerMethod(
        method(Subject.class, "doAs", Subject.class, java.security.PrivilegedExceptionAction.class),
        ExecutableMode.INVOKE);
    hints.reflection().registerMethod(method(Subject.class, "current"), ExecutableMode.INVOKE);
    hints.reflection().registerMethod(
        method(Subject.class, "callAs", Subject.class, java.util.concurrent.Callable.class),
        ExecutableMode.INVOKE);
  }

  private static Method method(Class<?> type, String name, Class<?>... parameterTypes) {
    try {
      return type.getDeclaredMethod(name, parameterTypes);
    } catch (NoSuchMethodException exception) {
      throw new IllegalStateException("Missing JDK method required for Kafka native SASL compatibility", exception);
    }
  }
}
