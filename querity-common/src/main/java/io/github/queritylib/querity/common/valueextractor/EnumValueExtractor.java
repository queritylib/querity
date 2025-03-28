package io.github.queritylib.querity.common.valueextractor;

public class EnumValueExtractor implements PropertyValueExtractor<Object> {
  @Override
  public boolean canHandle(Class<?> propertyType) {
    return propertyType.isEnum();
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Object extractValue(Class<?> propertyType, Object value) {
    if (value == null || isValueOfType(value, propertyType))
      return value;
    return Enum.valueOf((Class<Enum>) propertyType, (String) value);
  }

  private boolean isValueOfType(Object value, Class<?> propertyType) {
    return propertyType.isAssignableFrom(value.getClass());
  }
}
