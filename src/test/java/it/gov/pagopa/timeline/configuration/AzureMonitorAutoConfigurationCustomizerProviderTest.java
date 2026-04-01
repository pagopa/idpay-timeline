package it.gov.pagopa.timeline.configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import it.gov.pagopa.common.utils.MemoryAppender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AzureMonitorAutoConfigurationCustomizerProviderTest {

  private static final String MISSING_CONNECTION_STRING_WARNING =
      "Application Insights connection string is not configured";
  private static MemoryAppender memoryAppender;

  @BeforeAll
  static void configureMemoryAppender() {
    memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    memoryAppender.start();
  }

  @BeforeEach
  void clearMemoryAppender() {
    ch.qos.logback.classic.Logger logger =
        (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(AzureMonitorAutoConfigurationCustomizerProvider.class);
    logger.setLevel(Level.WARN);
    logger.addAppender(memoryAppender);
    memoryAppender.reset();
  }

  @Test
  void shouldDisableAzureMonitorExportersWhenConnectionStringIsMissing() {
    Map<String, String> overrides =
        AzureMonitorAutoConfigurationCustomizerProvider.customizeAzureMonitorProperties(
            new TestConfigProperties(Map.of(
                "otel.traces.exporter", "azure_monitor",
                "otel.metrics.exporter", "azure_monitor,prometheus",
                "otel.logs.exporter", "logging")));

    assertThat(overrides)
        .containsEntry("otel.traces.exporter", "none")
        .containsEntry("otel.metrics.exporter", "prometheus")
        .containsEntry("otel.logs.exporter", "logging");
    assertThat(memoryAppender.contains(Level.WARN, MISSING_CONNECTION_STRING_WARNING)).isTrue();
  }

  @Test
  void shouldKeepConfiguredExportersWhenConnectionStringIsPresent() {
    Map<String, String> overrides =
        AzureMonitorAutoConfigurationCustomizerProvider.customizeAzureMonitorProperties(
            new TestConfigProperties(Map.of(
                "applicationinsights.connection.string", "InstrumentationKey=test",
                "otel.traces.exporter", "azure_monitor")));

    assertThat(overrides).isEmpty();
    assertThat(memoryAppender.contains(Level.WARN, MISSING_CONNECTION_STRING_WARNING)).isFalse();
  }

  @Test
  void shouldBridgeSpringAzureConnectionStringWhenPresent() {
    Map<String, String> overrides =
        AzureMonitorAutoConfigurationCustomizerProvider.customizeAzureMonitorProperties(
            new TestConfigProperties(Map.of(
                "spring.cloud.azure.monitor.connection-string", "InstrumentationKey=test",
                "otel.traces.exporter", "azure_monitor")));

    assertThat(overrides)
        .containsEntry("applicationinsights.connection.string", "InstrumentationKey=test");
    assertThat(memoryAppender.contains(Level.WARN, MISSING_CONNECTION_STRING_WARNING)).isFalse();
  }

  @Test
  void shouldDisableOtelSdkWhenAzureMonitorIsTheOnlyExporter() {
    Map<String, String> overrides =
        AzureMonitorAutoConfigurationCustomizerProvider.customizeAzureMonitorProperties(
            new TestConfigProperties(Map.of(
                "otel.traces.exporter", "azure_monitor",
                "otel.metrics.exporter", "azure_monitor",
                "otel.logs.exporter", "azure_monitor")));

    assertThat(overrides)
        .containsEntry("otel.traces.exporter", "none")
        .containsEntry("otel.metrics.exporter", "none")
        .containsEntry("otel.logs.exporter", "none")
        .containsEntry("otel.sdk.disabled", "true");
  }

  @Test
  void shouldRunAfterOtherAutoConfigurationProviders() {
    assertThat(new AzureMonitorAutoConfigurationCustomizerProvider().order())
        .isEqualTo(Integer.MAX_VALUE);
  }

  private record TestConfigProperties(Map<String, String> values) implements ConfigProperties {

    @Override
    public String getString(String name) {
      return values.get(name);
    }

    @Override
    public Boolean getBoolean(String name) {
      return null;
    }

    @Override
    public Integer getInt(String name) {
      return null;
    }

    @Override
    public Long getLong(String name) {
      return null;
    }

    @Override
    public Double getDouble(String name) {
      return null;
    }

    @Override
    public Duration getDuration(String name) {
      return null;
    }

    @Override
    public List<String> getList(String name) {
      return null;
    }

    @Override
    public Map<String, String> getMap(String name) {
      return null;
    }
  }
}
