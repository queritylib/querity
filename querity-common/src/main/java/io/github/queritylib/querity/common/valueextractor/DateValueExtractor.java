package io.github.queritylib.querity.common.valueextractor;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    return getDateValue(propertyType, value.toString());
  }

  private static boolean isDateType(Class<?> cls) {
    return Date.class.isAssignableFrom(cls);
  }

  /**
   * Parses a string into the requested {@link Date} subtype. The {@code java.sql.*} subtypes are
   * parsed from their ISO-8601 representations (date, time, date-time), so that comparisons against
   * function expressions typed as {@code java.sql.Date}/{@code Time}/{@code Timestamp} (e.g.
   * {@code CURRENT_DATE}, {@code CURRENT_TIME}, {@code CURRENT_TIMESTAMP}) work. A plain
   * {@code java.util.Date} target keeps the historical ISO-instant behavior.
   */
  private static Date getDateValue(Class<?> propertyType, String value) {
    if (java.sql.Date.class.equals(propertyType))
      return java.sql.Date.valueOf(LocalDate.parse(value));
    if (Time.class.equals(propertyType))
      return Time.valueOf(LocalTime.parse(value));
    if (Timestamp.class.equals(propertyType))
      return Timestamp.valueOf(LocalDateTime.parse(value));
    return Date.from(Instant.parse(value));
  }
}
