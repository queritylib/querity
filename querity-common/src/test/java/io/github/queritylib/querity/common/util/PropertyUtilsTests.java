package io.github.queritylib.querity.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyUtilsTests {

  public static Stream<Arguments> provideBeanPropertiesAndTypes() {
    return Stream.of(
        Arguments.of(MyClass.class, "stringValue", String.class),
        Arguments.of(MyClass.class, "intValue", int.class),
        Arguments.of(MyClass.class, "integerValue", Integer.class),
        Arguments.of(MyClass.class, "bigDecimalValue", BigDecimal.class),
        Arguments.of(MyClass.class, "doubleValue", double.class),
        Arguments.of(MyClass.class, "nested.stringValue", String.class)
    );
  }

  @ParameterizedTest
  @MethodSource("provideBeanPropertiesAndTypes")
  void givenBeanProperty_whenGetPropertyType_thenReturnCorrectType(Class<?> beanClass, String propertyPath, Class<?> expectedPropertyType) {
    assertThat(PropertyUtils.getPropertyType(beanClass, propertyPath)).isEqualTo(expectedPropertyType);
  }

  @Test
  void givenNoxExistingBeanProperty_whenGetPropertyType_thenThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> PropertyUtils.getPropertyType(MyClass.class, "nonExisting"),
        "Property nonExisting not found in class MyClass");
  }

  @Test
  void givenNull_whenGetActualPropertyValue_thenReturnNull() {
    assertThat(PropertyUtils.getActualPropertyValue(MyClass.class, "stringValue", null)).isNull();
  }

  public static Stream<Arguments> provideValuesAndTypes() {
    return Stream.of(
        Arguments.of(MyClass.class, "stringValue", "test", String.class),
        Arguments.of(MyClass.class, "intValue", "1", Long.class),
        Arguments.of(MyClass.class, "intValue", 1, Integer.class),
        Arguments.of(MyClass.class, "integerValue", "1", Long.class),
        Arguments.of(MyClass.class, "integerValue", 1, Integer.class),
        Arguments.of(MyClass.class, "bigDecimalValue", "42.00", BigDecimal.class),
        Arguments.of(MyClass.class, "doubleValue", "1.2", BigDecimal.class),
        Arguments.of(MyClass.class, "doubleValue", 1.2, Double.class),
        Arguments.of(MyClass.class, "nested.stringValue", "test", String.class),
        Arguments.of(MyClass.class, "stringList", "test", String.class),
        Arguments.of(MyClass.class, "nestedList.stringValue", "test", String.class),
        Arguments.of(MyClass.class, "stringValue", List.of("test"), Object[].class),
        Arguments.of(MyClass.class, "stringValue", new Object[]{"test"}, Object[].class)
    );
  }

  @ParameterizedTest
  @MethodSource("provideValuesAndTypes")
  void givenValue_whenGetActualPropertyValue_thenReturnValueWithCorrectType(Class<?> beanClass, String propertyPath, Object value, Class<?> expectedType) {
    assertThat(PropertyUtils.getActualPropertyValue(beanClass, propertyPath, value).getClass()).isEqualTo(expectedType);
  }

  @Test
  void givenNull_whenGetActualValue_thenReturnNull() {
    assertThat(PropertyUtils.getActualValue(String.class, null)).isNull();
  }

  @Test
  void givenNullTargetType_whenGetActualValue_thenReturnRawValue() {
    assertThat(PropertyUtils.getActualValue(null, "raw")).isEqualTo("raw");
  }

  public static Stream<Arguments> provideTargetTypesAndValues() {
    return Stream.of(
      Arguments.of(String.class, "test", "test"),
      Arguments.of(Integer.class, "42", 42L),
      Arguments.of(BigDecimal.class, "42.00", new BigDecimal("42.00")),
      Arguments.of(LocalDate.class, "2026-06-22", LocalDate.of(2026, 6, 22)),
      Arguments.of(java.sql.Date.class, "2026-06-22", java.sql.Date.valueOf(LocalDate.of(2026, 6, 22)))
    );
  }

  @ParameterizedTest
  @MethodSource("provideTargetTypesAndValues")
  void givenValue_whenGetActualValue_thenReturnConvertedValue(Class<?> targetType, Object value, Object expectedValue) {
    assertThat(PropertyUtils.getActualValue(targetType, value)).isEqualTo(expectedValue);
  }

  @Test
  void givenIterable_whenGetActualValue_thenReturnArrayOfConvertedValues() {
    assertThat(PropertyUtils.getActualValue(LocalDate.class, List.of("2026-06-22", "2026-06-23")))
      .isEqualTo(new Object[]{LocalDate.of(2026, 6, 22), LocalDate.of(2026, 6, 23)});
  }

  @Test
  void givenArray_whenGetActualValue_thenReturnArrayOfConvertedValues() {
    assertThat(PropertyUtils.getActualValue(LocalDate.class, new Object[]{"2026-06-22", "2026-06-23"}))
      .isEqualTo(new Object[]{LocalDate.of(2026, 6, 22), LocalDate.of(2026, 6, 23)});
  }

  public static class MyClass {
    private String stringValue;
    private int intValue;
    private Integer integerValue;
    private BigDecimal bigDecimalValue;
    private double doubleValue;
    private MyNestedClass nested;
    private List<String> stringList;
    private List<MyNestedClass> nestedList;

    public static class MyNestedClass {
      private String stringValue;
    }
  }
}
