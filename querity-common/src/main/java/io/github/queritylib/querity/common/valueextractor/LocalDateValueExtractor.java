package io.github.queritylib.querity.common.valueextractor;

import java.time.LocalDate;

public class LocalDateValueExtractor implements PropertyValueExtractor<LocalDate> {

  @Override
  public boolean canHandle(Class<?> propertyType) {
    return isLocalDateType(propertyType);
  }

  @Override
  public LocalDate extractValue(Class<?> propertyType, Object value) {
    if (value == null || isLocalDateType(value.getClass()))
      return (LocalDate) value;
    return getLocalDateValue(value.toString());
  }

  private static boolean isLocalDateType(Class<?> cls) {
    return LocalDate.class.isAssignableFrom(cls);
  }

  private static LocalDate getLocalDateValue(String value) {
    return LocalDate.parse(value);
  }
}
