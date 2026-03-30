package it.gov.pagopa.timeline.configuration;

import it.gov.pagopa.common.logback.IgnoreCasePropertyEqualityCondition;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class NativeRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.reflection().registerType(
        IgnoreCasePropertyEqualityCondition.class,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.INVOKE_PUBLIC_METHODS);
    hints.resources().registerPattern("org/joda/time/tz/data/**");
  }
}
