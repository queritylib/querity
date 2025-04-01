package io.github.queritylib.querity.common.mapping;

import lombok.Builder;
import lombok.Singular;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Builder
public class SimplePropertyNameMapper implements PropertyNameMapper {

  /**
   * A map of property names to their mapped values.
   * <p>
   * The keys are the original property names, and the values are the mapped property names.
   */
  @Singular("mapping")
  private final Map<String, String> mappings;
  
  /**
   * A flag indicating whether to perform recursive mapping.
   * <p>
   * If true, the mapper will look for mappings in parent property names.
   */
  @Builder.Default
  private final boolean recursive = true;

  @Override
  public String mapPropertyName(String propertyName) {
    Optional<String> mapping = findMapping(propertyName);
    return mapping.orElse(propertyName);
  }

  private Optional<String> findMapping(String propertyName) {
    Optional<String> result = Optional.ofNullable(mappings.get(propertyName));
    if (recursive && result.isEmpty() && propertyName.contains("."))
      result = findMapping(getParentPropertyName(propertyName))
          .map(prefix -> prefix + "." + getPropertyName(propertyName));
    return result;
  }

  private static String getPropertyName(String propertyName) {
    String[] propertyPath = propertyName.split("\\.");
    return propertyPath[propertyPath.length - 1];
  }

  private static String getParentPropertyName(String propertyName) {
    String[] propertyPath = propertyName.split("\\.");
    String[] prefix = Arrays.copyOfRange(propertyPath, 0, propertyPath.length - 1);
    return String.join(".", prefix);
  }
}
