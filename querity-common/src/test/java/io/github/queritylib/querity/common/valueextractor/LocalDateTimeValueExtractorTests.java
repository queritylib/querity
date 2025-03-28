package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

class LocalDateTimeValueExtractorTests extends AbstractPropertyValueExtractorTests {

  @Override
  protected PropertyValueExtractor<?> getValueExtractor() {
    return new LocalDateTimeValueExtractor();
  }

  public static Stream<Arguments> provideTypesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, false),
        Arguments.of(Integer.class, false),
        Arguments.of(String.class, false),
        Arguments.of(Boolean.class, false),
        Arguments.of(LocalDate.class, false),
        Arguments.of(LocalDateTime.class, true),
        Arguments.of(ZonedDateTime.class, false)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    LocalDateTime testValue = LocalDateTime.of(2021, 4, 17, 4, 30, 0);
    return Stream.of(
        Arguments.of(LocalDateTime.class, null, null),
        Arguments.of(LocalDateTime.class, "2021-06-09T13:45:15", LocalDateTime.of(2021, 6, 9, 13, 45, 15)),
        Arguments.of(LocalDateTime.class, testValue, testValue)
    );
  }
}
