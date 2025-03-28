package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.util.stream.Stream;

class BooleanValueExtractorTests extends AbstractPropertyValueExtractorTests {

  @Override
  protected PropertyValueExtractor<?> getValueExtractor() {
    return new BooleanValueExtractor();
  }

  public static Stream<Arguments> provideTypesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, false),
        Arguments.of(Integer.class, false),
        Arguments.of(String.class, false),
        Arguments.of(Boolean.class, true)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    return Stream.of(
        Arguments.of(boolean.class, null, null),
        Arguments.of(boolean.class, "true", Boolean.TRUE),
        Arguments.of(boolean.class, "false", Boolean.FALSE),
        Arguments.of(Boolean.class, true, Boolean.TRUE),
        Arguments.of(Boolean.class, false, Boolean.FALSE),
        Arguments.of(Boolean.class, Boolean.TRUE, Boolean.TRUE),
        Arguments.of(Boolean.class, Boolean.FALSE, Boolean.FALSE),
        Arguments.of(Boolean.class, "notABoolean", Boolean.FALSE)
    );
  }
}
