package io.github.queritylib.querity.common.valueextractor;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NumericValueExtractor implements PropertyValueExtractor<Number> {

  @Override
  public boolean canHandle(Class<?> propertyType) {
    return isNumericType(propertyType);
  }

  @Override
  public Number extractValue(Class<?> propertyType, Object value) {
    if (value == null || isNumericType(value.getClass()))
      return (Number) value;  // at this point we're sure it's not primitive anymore because it's been auto-boxed
    return getNumericValue(propertyType, value.toString());
  }

  private static final Set<Class<?>> PRIMITIVE_NUMBERS = Stream
      .of(int.class, long.class, float.class,
          double.class, byte.class, short.class)
      .collect(Collectors.toSet());

  private static boolean isNumericType(Class<?> cls) {
    if (cls.isPrimitive()) {
      return PRIMITIVE_NUMBERS.contains(cls);
    } else {
      return Number.class.isAssignableFrom(cls);
    }
  }

  private static Number getNumericValue(Class<?> propertyType, String value) {
    if (!NumberUtils.isParsable(value)) throw new IllegalArgumentException(String.format("Not a number: %s", value));
    return isFloatingPointNumber(propertyType) ?
        new BigDecimal(value) :
        Long.valueOf(value);
  }

  private static final Set<Class<?>> FLOATING_POINT_NUMBERS = Stream
      .of(float.class, double.class, BigDecimal.class)
      .collect(Collectors.toSet());

  private static boolean isFloatingPointNumber(Class<?> cls) {
    return FLOATING_POINT_NUMBERS.contains(cls);
  }
}
