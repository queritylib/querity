package io.github.queritylib.querity.common.valueextractor;

public class StringValueExtractor implements PropertyValueExtractor<String> {
  @Override
  public boolean canHandle(Class<?> propertyType) {
    return String.class.equals(propertyType);
  }

  @Override
  public String extractValue(Class<?> propertyType, Object value) {
    return value.toString();
  }
}
