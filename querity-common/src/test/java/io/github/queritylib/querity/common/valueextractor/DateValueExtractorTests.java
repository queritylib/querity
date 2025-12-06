package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.stream.Stream;

class DateValueExtractorTests extends AbstractPropertyValueExtractorTests {

  @Override
  protected PropertyValueExtractor<?> getValueExtractor() {
    return new DateValueExtractor();
  }

  public static Stream<Arguments> provideTypesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, false),
        Arguments.of(Integer.class, false),
        Arguments.of(String.class, false),
        Arguments.of(Boolean.class, false),
        Arguments.of(LocalDate.class, false),
        Arguments.of(LocalDateTime.class, false),
        Arguments.of(ZonedDateTime.class, false),
        Arguments.of(Date.class, true),
        Arguments.of(java.sql.Date.class, true)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    Date testValue = Date.from(Instant.parse("2021-04-17T10:15:30Z"));
    return Stream.of(
        Arguments.of(Date.class, null, null),
        Arguments.of(Date.class, "2021-06-09T12:30:00Z", Date.from(Instant.parse("2021-06-09T12:30:00Z"))),
        Arguments.of(Date.class, testValue, testValue)
    );
  }
}
