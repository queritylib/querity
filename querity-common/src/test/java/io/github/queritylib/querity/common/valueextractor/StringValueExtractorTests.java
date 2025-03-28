package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringValueExtractorTests {

  private final StringValueExtractor valueExtractor = new StringValueExtractor();

  public static Stream<Arguments> provideClassesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, false),
        Arguments.of(Integer.class, false),
        Arguments.of(String.class, true),
        Arguments.of(Boolean.class, false)
    );
  }

  @ParameterizedTest
  @MethodSource("provideClassesAndCanHandle")
  void givenClass_whenCanHandle_thenReturnTrueIfString(Class<?> clazz, boolean expectedCanHandle) {
    assertThat(valueExtractor.canHandle(clazz)).isEqualTo(expectedCanHandle);
  }

  @Test
  void givenString_whenExtractValue_theReturnTheSameString() {
    assertThat(valueExtractor.extractValue(String.class, "test")).isEqualTo("test");
  }
}
