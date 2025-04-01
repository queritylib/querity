package io.github.queritylib.querity.common.mapping;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePropertyNameMapperTests {

  private static final Map<String, String> MAPPING = Map.of(
      "a", "b",
      "c", "d",
      "e", "f",
      "e.g", "f.h"
  );

  public static Stream<Arguments> recursiveMapperArgumentsProvider() {
    return Stream.of(
        Arguments.of("a", "b"),
        Arguments.of("c", "d"),
        Arguments.of("e", "f"),
        Arguments.of("x", "x"),
        Arguments.of("x.y", "x.y"),
        Arguments.of("e.g", "f.h"),
        Arguments.of("e.i", "f.i"),
        Arguments.of("e.g.j", "f.h.j"),
        Arguments.of("e.i.j", "f.i.j")
    );
  }

  @ParameterizedTest
  @MethodSource("recursiveMapperArgumentsProvider")
  void testRecursiveMapping(String propertyName, String expected) {
    var recursiveMapper = SimplePropertyNameMapper.builder()
        .mappings(MAPPING)
        .build();
    String result = recursiveMapper.mapPropertyName(propertyName);
    assertThat(result).isEqualTo(expected);
  }

  public static Stream<Arguments> nonRecursiveMapperArgumentsProvider() {
    return Stream.of(
        Arguments.of("a", "b"),
        Arguments.of("c", "d"),
        Arguments.of("e", "f"),
        Arguments.of("x", "x"),
        Arguments.of("x.y", "x.y"),
        Arguments.of("e.g", "f.h"),
        Arguments.of("e.i", "e.i"),
        Arguments.of("e.g.j", "e.g.j"),
        Arguments.of("e.i.j", "e.i.j")
    );
  }

  @ParameterizedTest
  @MethodSource("nonRecursiveMapperArgumentsProvider")
  void testNonRecursiveMapping(String propertyName, String expected) {
    var recursiveMapper = SimplePropertyNameMapper.builder()
        .recursive(false)
        .mappings(MAPPING)
        .build();
    String result = recursiveMapper.mapPropertyName(propertyName);
    assertThat(result).isEqualTo(expected);
  }
}
