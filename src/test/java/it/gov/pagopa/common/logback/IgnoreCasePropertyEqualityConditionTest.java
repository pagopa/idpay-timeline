package it.gov.pagopa.common.logback;

import ch.qos.logback.core.ContextBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IgnoreCasePropertyEqualityConditionTest {

  @Test
  void shouldMatchPropertyIgnoringCase() {
    ContextBase context = new ContextBase();
    context.putProperty("ENABLE_FILE_APPENDER_VALUE", "TrUe");

    IgnoreCasePropertyEqualityCondition condition = new IgnoreCasePropertyEqualityCondition();
    condition.setContext(context);
    condition.setLocalPropertyContainer(context);
    condition.setKey("ENABLE_FILE_APPENDER_VALUE");
    condition.setValue("true");
    condition.start();

    Assertions.assertTrue(condition.evaluate());
  }

  @Test
  void shouldReturnFalseWhenPropertyDoesNotMatch() {
    ContextBase context = new ContextBase();
    context.putProperty("ENABLE_FILE_APPENDER_VALUE", "false");

    IgnoreCasePropertyEqualityCondition condition = new IgnoreCasePropertyEqualityCondition();
    condition.setContext(context);
    condition.setLocalPropertyContainer(context);
    condition.setKey("ENABLE_FILE_APPENDER_VALUE");
    condition.setValue("true");
    condition.start();

    Assertions.assertFalse(condition.evaluate());
  }
}
