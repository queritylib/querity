package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

class ZonedDateTimeValueExtractorTests extends AbstractPropertyValueExtractorTests {

  @Override
  protected PropertyValueExtractor<?> getValueExtractor() {
    return new ZonedDateTimeValueExtractor();
  }

  public static Stream<Arguments> provideTypesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, false),
        Arguments.of(Integer.class, false),
        Arguments.of(String.class, false),
        Arguments.of(Boolean.class, false),
        Arguments.of(LocalDate.class, false),
        Arguments.of(LocalDateTime.class, false),
        Arguments.of(ZonedDateTime.class, true)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    ZonedDateTime testValue = ZonedDateTime.of(2021, 4, 17, 4, 30, 0, 0, ZoneOffset.ofHours(2));
    return Stream.of(
        Arguments.of(ZonedDateTime.class, null, null),
        Arguments.of(ZonedDateTime.class, "2021-06-09T13:45:15.000+01:00", ZonedDateTime.of(2021, 6, 9, 13, 45, 15, 0, ZoneOffset.ofHours(1))),
        Arguments.of(ZonedDateTime.class, testValue, testValue)
    );
  }
}
