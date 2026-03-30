package it.gov.pagopa.timeline.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.integration.support.management.IntegrationManagedResource;

import static org.assertj.core.api.Assertions.assertThat;

class FilteringIntegrationJmxAttributeSourceTest {

  @Test
  void shouldIgnoreLegacyCurrencyTimeLimitAttribute() {
    FilteringIntegrationJmxAttributeSource source = new FilteringIntegrationJmxAttributeSource();

    org.springframework.jmx.export.metadata.ManagedResource managedResource =
        source.getManagedResource(SampleManagedResource.class);

    assertThat(managedResource).isNotNull();
    assertThat(managedResource.getObjectName()).isEqualTo("bean:name=test");
  }

  @IntegrationManagedResource(objectName = "bean:name=test", currencyTimeLimit = 30)
  public static class SampleManagedResource {
  }
}
