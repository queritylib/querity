package io.github.queritylib.querity.common.valueextractor;

import java.time.Instant;
import java.util.Date;

public class DateValueExtractor implements PropertyValueExtractor<Date> {

  @Override
  public boolean canHandle(Class<?> propertyType) {
    return isDateType(propertyType);
  }

  @Override
  public Date extractValue(Class<?> propertyType, Object value) {
    if (value == null || isDateType(value.getClass()))
      return (Date) value;
    return getDateValue(value.toString());
  }

  private static boolean isDateType(Class<?> cls) {
    return Date.class.isAssignableFrom(cls);
  }

  private static Date getDateValue(String value) {
    return Date.from(Instant.parse(value));
  }
}
