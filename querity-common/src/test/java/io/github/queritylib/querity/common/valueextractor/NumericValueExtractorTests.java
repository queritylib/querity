package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NumericValueExtractorTests extends AbstractPropertyValueExtractorTests {

  @Override
  protected PropertyValueExtractor<?> getValueExtractor() {
    return new NumericValueExtractor();
  }

  public static Stream<Arguments> provideTypesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, true),
        Arguments.of(Integer.class, true),
        Arguments.of(String.class, false),
        Arguments.of(Boolean.class, false)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    return Stream.of(
        Arguments.of(Integer.class, null, null),
        Arguments.of(long.class, "11", 11L),
        Arguments.of(BigDecimal.class, "0.12", new BigDecimal("0.12")),
        Arguments.of(Long.class, "0", 0L),
        Arguments.of(BigDecimal.class, "42.00", new BigDecimal("42.00")),
        Arguments.of(BigDecimal.class, new BigDecimal("420.00"), new BigDecimal("420.00")),
        Arguments.of(int.class, 420, 420),
        Arguments.of(short.class, (short) 1, (short) 1)
    );
  }

  @Test
  void givenInvalidNumber_whenExtractValue_thenThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> valueExtractor.extractValue(Number.class, "notANumber"),
        "Not a number: notANumber");

  }
}
