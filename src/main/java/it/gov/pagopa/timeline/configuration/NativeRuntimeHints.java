package it.gov.pagopa.timeline.configuration;

import com.azure.monitor.opentelemetry.autoconfigure.implementation.AzureMonitorLogRecordExporterProvider;
import com.azure.monitor.opentelemetry.autoconfigure.implementation.AzureMonitorMetricExporterProvider;
import com.azure.monitor.opentelemetry.autoconfigure.implementation.AzureMonitorSpanExporterProvider;
import it.gov.pagopa.common.logback.IgnoreCasePropertyEqualityCondition;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import javax.security.auth.Subject;
import java.lang.reflect.Method;

public class NativeRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.reflection().registerType(
        IgnoreCasePropertyEqualityCondition.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_METHODS);
    hints.reflection().registerType(
        AzureMonitorAutoConfigurationCustomizerProvider.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
    hints.reflection().registerType(
        AzureMonitorSpanExporterProvider.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
    hints.reflection().registerType(
        AzureMonitorMetricExporterProvider.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
    hints.reflection().registerType(
        AzureMonitorLogRecordExporterProvider.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
    registerKafkaSaslCompatibilityHints(hints);
    hints.resources().registerPattern("org/joda/time/tz/data/**");
    hints.resources().registerPattern("META-INF/services/io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider");
    hints.resources().registerPattern("META-INF/services/io.opentelemetry.sdk.autoconfigure.spi.traces.ConfigurableSpanExporterProvider");
    hints.resources().registerPattern("META-INF/services/io.opentelemetry.sdk.autoconfigure.spi.metrics.ConfigurableMetricExporterProvider");
    hints.resources().registerPattern("META-INF/services/io.opentelemetry.sdk.autoconfigure.spi.logs.ConfigurableLogRecordExporterProvider");
  }

  private static void registerKafkaSaslCompatibilityHints(RuntimeHints hints) {
    hints.reflection().registerType(Subject.class);
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
