package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

class UUIDValueExtractorTests extends AbstractPropertyValueExtractorTests {

  @Override
  protected PropertyValueExtractor<?> getValueExtractor() {
    return new UUIDValueExtractor();
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
        Arguments.of(Date.class, false),
        Arguments.of(UUID.class, true)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    UUID testValue = UUID.fromString("8c178b3d-99b1-4d82-a54d-477157d30517");
    return Stream.of(
        Arguments.of(UUID.class, null, null),
        Arguments.of(UUID.class, testValue.toString(), testValue),
        Arguments.of(UUID.class, testValue, testValue)
    );
  }
}
