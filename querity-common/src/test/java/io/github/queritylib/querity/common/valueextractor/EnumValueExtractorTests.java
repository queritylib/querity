package io.github.queritylib.querity.common.valueextractor;

import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.util.stream.Stream;

class EnumValueExtractorTests extends AbstractPropertyValueExtractorTests {

  @Override
  protected PropertyValueExtractor<?> getValueExtractor() {
    return new EnumValueExtractor();
  }

  public static Stream<Arguments> provideTypesAndCanHandle() {
    return Stream.of(
        Arguments.of(BigDecimal.class, false),
        Arguments.of(Integer.class, false),
        Arguments.of(String.class, false),
        Arguments.of(Boolean.class, false),
        Arguments.of(MyEnum.class, true)
    );
  }

  public static Stream<Arguments> provideInputAndExpectedExtractedValue() {
    return Stream.of(
        Arguments.of(MyEnum.class, null, null),
        Arguments.of(MyEnum.class, "ONE", MyEnum.ONE),
        Arguments.of(MyEnum.class, "TWO", MyEnum.TWO),
        Arguments.of(MyEnum.class, MyEnum.ONE, MyEnum.ONE),
        Arguments.of(MyEnum.class, MyEnum.TWO, MyEnum.TWO)
    );
  }

  public enum MyEnum {
    ONE, TWO
  }
}
