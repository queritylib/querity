package io.github.queritylib.querity.common.valueextractor;

import java.time.LocalDateTime;

public class LocalDateTimeValueExtractor implements PropertyValueExtractor<LocalDateTime> {

  @Override
  public boolean canHandle(Class<?> propertyType) {
    return isLocalDateTimeType(propertyType);
  }

  @Override
  public LocalDateTime extractValue(Class<?> propertyType, Object value) {
    if (value == null || isLocalDateTimeType(value.getClass()))
      return (LocalDateTime) value;
    return getLocalDateTimeValue(value.toString());
  }

  private static boolean isLocalDateTimeType(Class<?> cls) {
    return LocalDateTime.class.isAssignableFrom(cls);
  }

  private static LocalDateTime getLocalDateTimeValue(String value) {
    return LocalDateTime.parse(value);
  }
}
