package io.github.queritylib.querity.common.valueextractor;

public class BooleanValueExtractor implements PropertyValueExtractor<Boolean> {

  @Override
  public boolean canHandle(Class<?> propertyType) {
    return isBooleanType(propertyType);
  }

  @Override
  public Boolean extractValue(Class<?> propertyType, Object value) {
    if (value == null || isBooleanType(value.getClass()))
      return (Boolean) value;  // at this point we're sure it's not primitive anymore because it's been auto-boxed
    return getBooleanValue(value.toString());
  }

  private static boolean isBooleanType(Class<?> cls) {
    if (cls.isPrimitive()) {
      return boolean.class.equals(cls);
    } else {
      return Boolean.class.isAssignableFrom(cls);
    }
  }

  private static Boolean getBooleanValue(String value) {
    return Boolean.valueOf(value);
  }
}
