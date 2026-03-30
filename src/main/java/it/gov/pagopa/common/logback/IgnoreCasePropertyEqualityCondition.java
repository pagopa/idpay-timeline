package it.gov.pagopa.common.logback;

import ch.qos.logback.core.boolex.PropertyConditionBase;

public class IgnoreCasePropertyEqualityCondition extends PropertyConditionBase {

  private String key;
  private String value;

  @Override
  public void start() {
    if (key == null || key.isBlank()) {
      addError("The \"key\" property must be set");
      return;
    }
    if (value == null) {
      addError("The \"value\" property must be set");
      return;
    }
    super.start();
  }

  @Override
  public boolean evaluate() {
    if (!isStarted()) {
      return false;
    }
    String propertyValue = property(key);
    return propertyValue != null && propertyValue.equalsIgnoreCase(value);
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
