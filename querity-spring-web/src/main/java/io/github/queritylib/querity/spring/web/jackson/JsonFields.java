package io.github.queritylib.querity.spring.web.jackson;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Common JSON field name constants used across deserializers.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonFields {

  public static final String PROPERTY_NAME = "propertyName";
  public static final String FUNCTION = "function";
  public static final String VALUE = "value";
  public static final String PROPERTY_NAMES = "propertyNames";
  public static final String EXPRESSIONS = "expressions";
  public static final String DIRECTION = "direction";
}
