package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        Arguments.of(Date.class, true)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    Date testValue = Date.from(
        LocalDateTime.of(2021, 4, 17, 4, 30, 0)
            .toInstant(ZoneOffset.UTC));
    return Stream.of(
        Arguments.of(Date.class, null, null),
        Arguments.of(Date.class, "2021-06-09T13:45:15Z", Date.from(
            LocalDateTime.of(2021, 6, 9, 13, 45, 15)
                .toInstant(ZoneOffset.UTC))),
        Arguments.of(Date.class, testValue, testValue)
    );
  }
}
