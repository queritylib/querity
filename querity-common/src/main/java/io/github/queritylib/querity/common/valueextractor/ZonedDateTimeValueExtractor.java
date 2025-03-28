package io.github.queritylib.querity.common.valueextractor;

import java.time.ZonedDateTime;

public class ZonedDateTimeValueExtractor implements PropertyValueExtractor<ZonedDateTime> {

  @Override
  public boolean canHandle(Class<?> propertyType) {
    return isZonedDateTimeType(propertyType);
  }

  @Override
  public ZonedDateTime extractValue(Class<?> propertyType, Object value) {
    if (value == null || isZonedDateTimeType(value.getClass()))
      return (ZonedDateTime) value;
    return getZonedDateTimeValue(value.toString());
  }

  private static boolean isZonedDateTimeType(Class<?> cls) {
    return ZonedDateTime.class.isAssignableFrom(cls);
  }

  private static ZonedDateTime getZonedDateTimeValue(String value) {
    return ZonedDateTime.parse(value);
  }
}
