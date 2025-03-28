package io.github.queritylib.querity.common.valueextractor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class AbstractPropertyValueExtractorTests {

  protected final PropertyValueExtractor<?> valueExtractor = getValueExtractor();

  protected abstract PropertyValueExtractor<?> getValueExtractor();

  @ParameterizedTest
  @MethodSource("provideTypesAndCanHandle")
  void givenType_whenCanHandle_thenReturnTrueIfHandledType(Class<?> type, boolean expectedCanHandle) {
    Assertions.assertThat(valueExtractor.canHandle(type)).isEqualTo(expectedCanHandle);
  }

  @ParameterizedTest
  @MethodSource("provideInputAndExpectedExtractedValue")
  void givenInput_whenExtractValue_thenReturnTheExtractedValue(Class<?> propertyType, Object input, Object expectedExtractedValue) {
    Assertions.assertThat(valueExtractor.extractValue(propertyType, input)).isEqualTo(expectedExtractedValue);
  }
}
