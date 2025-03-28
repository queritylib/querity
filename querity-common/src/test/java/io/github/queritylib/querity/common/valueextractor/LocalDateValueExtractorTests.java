package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

class LocalDateValueExtractorTests extends AbstractPropertyValueExtractorTests {

  @Override
  protected PropertyValueExtractor<?> getValueExtractor() {
    return new LocalDateValueExtractor();
  }

  public static Stream<Arguments> provideTypesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, false),
        Arguments.of(Integer.class, false),
        Arguments.of(String.class, false),
        Arguments.of(Boolean.class, false),
        Arguments.of(LocalDate.class, true),
        Arguments.of(LocalDateTime.class, false),
        Arguments.of(ZonedDateTime.class, false)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    LocalDate testValue = LocalDate.of(2021, 4, 17);
    return Stream.of(
        Arguments.of(LocalDate.class, null, null),
        Arguments.of(LocalDate.class, "2021-06-09", LocalDate.of(2021, 6, 9)),
        Arguments.of(LocalDate.class, testValue, testValue)
    );
  }
}
