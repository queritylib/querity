package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyValueExtractorFactoryTests {

  public static Stream<Arguments> providePropertyTypeAndValueExtractor() {
    return Stream.of(
        Arguments.of(String.class, StringValueExtractor.class),
        Arguments.of(int.class, NumericValueExtractor.class),
        Arguments.of(Integer.class, NumericValueExtractor.class),
        Arguments.of(BigDecimal.class, NumericValueExtractor.class),
        Arguments.of(double.class, NumericValueExtractor.class),
        Arguments.of(boolean.class, BooleanValueExtractor.class),
        Arguments.of(Boolean.class, BooleanValueExtractor.class),
        Arguments.of(Date.class, DateValueExtractor.class),
        Arguments.of(LocalDate.class, LocalDateValueExtractor.class),
        Arguments.of(LocalDateTime.class, LocalDateTimeValueExtractor.class),
        Arguments.of(ZonedDateTime.class, ZonedDateTimeValueExtractor.class)
    );
  }

  @ParameterizedTest
  @MethodSource("providePropertyTypeAndValueExtractor")
  void givenBeanProperty_whenGetPropertyValueExtractor_thenReturnsCorrectValueExtractor(Class<?> propertyType, Class<? extends PropertyValueExtractor<?>> expectedValueExtractorClass) {
    PropertyValueExtractor<?> valueExtractor = PropertyValueExtractorFactory.getPropertyValueExtractor(propertyType);
    assertThat(valueExtractor.getClass()).isEqualTo(expectedValueExtractorClass);
  }
}
