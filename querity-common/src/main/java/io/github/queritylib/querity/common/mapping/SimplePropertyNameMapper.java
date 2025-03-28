package io.github.queritylib.querity.common.mapping;

import lombok.Builder;
import lombok.Singular;

import java.util.Map;

@Builder
public class SimplePropertyNameMapper implements PropertyNameMapper {

  @Singular("mapping")
  private final Map<String, String> propertyNameMapping;

  public SimplePropertyNameMapper(Map<String, String> propertyNameMapping) {
    this.propertyNameMapping = propertyNameMapping;
  }

  @Override
  public String mapPropertyName(String propertyName) {
    return propertyNameMapping.getOrDefault(propertyName, propertyName);
  }
}
