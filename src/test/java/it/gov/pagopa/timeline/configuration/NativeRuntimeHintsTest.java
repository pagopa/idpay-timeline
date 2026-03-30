package it.gov.pagopa.timeline.configuration;

import it.gov.pagopa.common.logback.IgnoreCasePropertyEqualityCondition;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class NativeRuntimeHintsTest {

  @Test
  void shouldRegisterNativeRuntimeHints() {
    RuntimeHints hints = new RuntimeHints();

    new NativeRuntimeHints().registerHints(hints, getClass().getClassLoader());

    assertThat(RuntimeHintsPredicates.reflection().onType(IgnoreCasePropertyEqualityCondition.class))
        .accepts(hints);
    assertThat(RuntimeHintsPredicates.resource().forResource("org/joda/time/tz/data/ZoneInfoMap"))
        .accepts(hints);
  }
}
