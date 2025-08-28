package io.github.queritylib.querity.common.valueextractor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertyValueExtractorFactory {

  public static final NoOpValueExtractor NO_OP_VALUE_EXTRACTOR = new NoOpValueExtractor();

  private static final List<PropertyValueExtractor<?>> extractors = Arrays.asList(
      new StringValueExtractor(),
      new NumericValueExtractor(),
      new BooleanValueExtractor(),
      new DateValueExtractor(),
      new LocalDateValueExtractor(),
      new LocalDateTimeValueExtractor(),
      new ZonedDateTimeValueExtractor(),
      new EnumValueExtractor(),
      new UUIDValueExtractor());

  @SuppressWarnings("java:S1452")
  public static PropertyValueExtractor<?> getPropertyValueExtractor(Class<?> propertyType) {
    return extractors.stream()
        .filter(e -> e.canHandle(propertyType))
        .findAny()
        .orElse(NO_OP_VALUE_EXTRACTOR);
  }
}
