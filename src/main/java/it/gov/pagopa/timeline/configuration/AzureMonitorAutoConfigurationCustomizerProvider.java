package it.gov.pagopa.timeline.configuration;

import com.azure.monitor.opentelemetry.autoconfigure.AzureMonitorAutoConfigure;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public final class AzureMonitorAutoConfigurationCustomizerProvider
    implements AutoConfigurationCustomizerProvider {

  private static final String APPLICATIONINSIGHTS_CONNECTION_STRING =
      "applicationinsights.connection.string";
  private static final String SPRING_AZURE_MONITOR_CONNECTION_STRING =
      "spring.cloud.azure.monitor.connection-string";
  private static final String AZURE_MONITOR_EXPORTER = "azure_monitor";
  private static final String NONE_EXPORTER = "none";
  private static final String OTEL_TRACES_EXPORTER = "otel.traces.exporter";
  private static final String OTEL_METRICS_EXPORTER = "otel.metrics.exporter";
  private static final String OTEL_LOGS_EXPORTER = "otel.logs.exporter";
  private static final String OTEL_SDK_DISABLED = "otel.sdk.disabled";
  private static final String TRUE_VALUE = "true";

  @Override
  public int order() {
    return Integer.MAX_VALUE;
  }

  @Override
  public void customize(AutoConfigurationCustomizer autoConfigurationCustomizer) {
    AzureMonitorAutoConfigure.customize(autoConfigurationCustomizer);
    autoConfigurationCustomizer.addPropertiesCustomizer(
        AzureMonitorAutoConfigurationCustomizerProvider::customizeAzureMonitorProperties);
  }

  static Map<String, String> customizeAzureMonitorProperties(
      ConfigProperties configProperties) {
    String applicationInsightsConnectionString =
        configProperties.getString(APPLICATIONINSIGHTS_CONNECTION_STRING);
    if (hasText(applicationInsightsConnectionString)) {
      return Map.of();
    }
    String springAzureConnectionString =
        configProperties.getString(SPRING_AZURE_MONITOR_CONNECTION_STRING);
    if (hasText(springAzureConnectionString)) {
      return Map.of(APPLICATIONINSIGHTS_CONNECTION_STRING, springAzureConnectionString);
    }
    if (!requiresAzureMonitor(configProperties)) {
      return Map.of();
    }
    log.warn(
        "Application Insights connection string is not configured: Azure Monitor telemetry export will be disabled");
    String tracesExporter = withoutAzureMonitor(configProperties.getString(OTEL_TRACES_EXPORTER));
    String metricsExporter = withoutAzureMonitor(configProperties.getString(OTEL_METRICS_EXPORTER));
    String logsExporter = withoutAzureMonitor(configProperties.getString(OTEL_LOGS_EXPORTER));

    Map<String, String> overrides = new LinkedHashMap<>();
    overrides.put(OTEL_TRACES_EXPORTER, tracesExporter);
    overrides.put(OTEL_METRICS_EXPORTER, metricsExporter);
    overrides.put(OTEL_LOGS_EXPORTER, logsExporter);
    if (shouldDisableSdk(tracesExporter, metricsExporter, logsExporter)) {
      overrides.put(OTEL_SDK_DISABLED, TRUE_VALUE);
    }
    return overrides;
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private static boolean requiresAzureMonitor(ConfigProperties configProperties) {
    return containsAzureMonitor(configProperties.getString(OTEL_TRACES_EXPORTER))
        || containsAzureMonitor(configProperties.getString(OTEL_METRICS_EXPORTER))
        || containsAzureMonitor(configProperties.getString(OTEL_LOGS_EXPORTER));
  }

  private static boolean containsAzureMonitor(String exporters) {
    return Arrays.stream((exporters == null ? "" : exporters).split(","))
        .map(String::trim)
        .anyMatch(AZURE_MONITOR_EXPORTER::equals);
  }

  private static boolean shouldDisableSdk(
      String tracesExporter, String metricsExporter, String logsExporter) {
    return NONE_EXPORTER.equals(tracesExporter)
        && NONE_EXPORTER.equals(metricsExporter)
        && NONE_EXPORTER.equals(logsExporter);
  }

  private static String withoutAzureMonitor(String exporters) {
    if (!hasText(exporters)) {
      return NONE_EXPORTER;
    }
    String filteredExporters = Arrays.stream(exporters.split(","))
        .map(String::trim)
        .filter(AzureMonitorAutoConfigurationCustomizerProvider::hasText)
        .filter(exporter -> !AZURE_MONITOR_EXPORTER.equals(exporter))
        .collect(
            Collectors.collectingAndThen(
                Collectors.toCollection(LinkedHashSet::new),
                values -> String.join(",", values)));
    return hasText(filteredExporters) ? filteredExporters : NONE_EXPORTER;
  }
}
