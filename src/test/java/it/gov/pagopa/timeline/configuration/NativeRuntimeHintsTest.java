package it.gov.pagopa.timeline.configuration;

import it.gov.pagopa.common.logback.IgnoreCasePropertyEqualityCondition;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;
import org.springframework.jmx.export.metadata.AbstractJmxAttribute;
import org.springframework.jmx.export.metadata.ManagedOperationParameter;
import org.springframework.jmx.export.metadata.ManagedResource;

import static org.assertj.core.api.Assertions.assertThat;

class NativeRuntimeHintsTest {

  @Test
  void shouldRegisterNativeRuntimeHints() {
    RuntimeHints hints = new RuntimeHints();

    new NativeRuntimeHints().registerHints(hints, getClass().getClassLoader());

    assertThat(RuntimeHintsPredicates.reflection().onType(IgnoreCasePropertyEqualityCondition.class))
        .accepts(hints);
    assertThat(RuntimeHintsPredicates.reflection().onType(AbstractJmxAttribute.class))
        .accepts(hints);
    assertThat(RuntimeHintsPredicates.reflection().onType(ManagedResource.class))
        .accepts(hints);
    assertThat(RuntimeHintsPredicates.reflection().onType(ManagedOperationParameter.class))
        .accepts(hints);
    assertThat(RuntimeHintsPredicates.resource().forResource("org/joda/time/tz/data/ZoneInfoMap"))
        .accepts(hints);
  }
}
