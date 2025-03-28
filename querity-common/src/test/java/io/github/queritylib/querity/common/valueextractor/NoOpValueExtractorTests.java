package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NoOpValueExtractorTests {

  private final NoOpValueExtractor valueExtractor = new NoOpValueExtractor();

  public static Stream<Arguments> provideClassesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, false),
        Arguments.of(Integer.class, false),
        Arguments.of(String.class, false),
        Arguments.of(Boolean.class, false)
    );
  }

  @ParameterizedTest
  @MethodSource("provideClassesAndCanHandle")
  void givenClass_whenCanHandle_thenReturnFalse(Class<?> clazz, boolean expectedCanHandle) {
    assertThat(valueExtractor.canHandle(clazz)).isEqualTo(expectedCanHandle);
  }

  @Test
  void givenNull_whenExtractValue_theReturnNull() {
    assertThat(valueExtractor.extractValue(Object.class, null)).isNull();
  }

  @Test
  void givenString_whenExtractValue_theReturnTheSameString() {
    assertThat(valueExtractor.extractValue(String.class, "test")).isEqualTo("test");
  }

  @Test
  void givenInteger_whenExtractValue_theReturnTheSameInteger() {
    assertThat(valueExtractor.extractValue(Integer.class, 1)).isEqualTo(1);
  }
}
