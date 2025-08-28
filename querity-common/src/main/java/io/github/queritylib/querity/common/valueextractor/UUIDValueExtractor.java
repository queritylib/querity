package io.github.queritylib.querity.common.valueextractor;

import java.util.UUID;

public class UUIDValueExtractor implements PropertyValueExtractor<UUID> {
  @Override
  public boolean canHandle(Class<?> propertyType) {
    return isUUIDType(propertyType);
  }

  @Override
  public UUID extractValue(Class<?> propertyType, Object value) {
    if (value == null || isUUIDType(value.getClass()))
      return (UUID) value;  // at this point we're sure it's not primitive anymore because it's been auto-boxed
    return UUID.fromString(value.toString());
  }

  private static boolean isUUIDType(Class<?> propertyType) {
    return UUID.class.isAssignableFrom(propertyType);
  }
}
