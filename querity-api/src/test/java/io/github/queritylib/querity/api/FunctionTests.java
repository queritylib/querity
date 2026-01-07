package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionTests {

  @Test
  void givenAllFunctions_whenGetArgumentCount_thenReturnExpectedCount() {
    for (Function function : Function.values()) {
      int count = function.getArgumentCount();
      // Variadic functions return -1
      if (function == Function.COALESCE || function == Function.CONCAT) {
        assertThat(count).isEqualTo(-1);
      } else {
        assertThat(count).isGreaterThanOrEqualTo(0);
      }
    }
  }

  @Test
  void givenAllFunctions_whenGetCategory_thenReturnNonNullCategory() {
    for (Function function : Function.values()) {
      assertThat(function.getCategory()).isNotNull();
    }
  }

  @Test
  void givenArithmeticFunctions_whenGetCategory_thenReturnArithmetic() {
    assertThat(Function.ABS.getCategory()).isEqualTo(Function.FunctionCategory.ARITHMETIC);
    assertThat(Function.SQRT.getCategory()).isEqualTo(Function.FunctionCategory.ARITHMETIC);
    assertThat(Function.MOD.getCategory()).isEqualTo(Function.FunctionCategory.ARITHMETIC);
  }

  @Test
  void givenStringFunctions_whenGetCategory_thenReturnString() {
    assertThat(Function.CONCAT.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
    assertThat(Function.SUBSTRING.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
    assertThat(Function.TRIM.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
    assertThat(Function.LTRIM.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
    assertThat(Function.RTRIM.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
    assertThat(Function.LOWER.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
    assertThat(Function.UPPER.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
    assertThat(Function.LENGTH.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
    assertThat(Function.LOCATE.getCategory()).isEqualTo(Function.FunctionCategory.STRING);
  }

  @Test
  void givenDateTimeFunctions_whenGetCategory_thenReturnDateTime() {
    assertThat(Function.CURRENT_DATE.getCategory()).isEqualTo(Function.FunctionCategory.DATE_TIME);
    assertThat(Function.CURRENT_TIME.getCategory()).isEqualTo(Function.FunctionCategory.DATE_TIME);
    assertThat(Function.CURRENT_TIMESTAMP.getCategory()).isEqualTo(Function.FunctionCategory.DATE_TIME);
  }

  @Test
  void givenConditionalFunctions_whenGetCategory_thenReturnConditional() {
    assertThat(Function.COALESCE.getCategory()).isEqualTo(Function.FunctionCategory.CONDITIONAL);
    assertThat(Function.NULLIF.getCategory()).isEqualTo(Function.FunctionCategory.CONDITIONAL);
  }

  @Test
  void givenAggregateFunctions_whenGetCategory_thenReturnAggregate() {
    assertThat(Function.COUNT.getCategory()).isEqualTo(Function.FunctionCategory.AGGREGATE);
    assertThat(Function.SUM.getCategory()).isEqualTo(Function.FunctionCategory.AGGREGATE);
    assertThat(Function.AVG.getCategory()).isEqualTo(Function.FunctionCategory.AGGREGATE);
    assertThat(Function.MIN.getCategory()).isEqualTo(Function.FunctionCategory.AGGREGATE);
    assertThat(Function.MAX.getCategory()).isEqualTo(Function.FunctionCategory.AGGREGATE);
  }

  @Test
  void givenAggregateFunctions_whenIsAggregate_thenReturnTrue() {
    assertThat(Function.COUNT.isAggregate()).isTrue();
    assertThat(Function.SUM.isAggregate()).isTrue();
    assertThat(Function.AVG.isAggregate()).isTrue();
    assertThat(Function.MIN.isAggregate()).isTrue();
    assertThat(Function.MAX.isAggregate()).isTrue();
  }

  @Test
  void givenNonAggregateFunctions_whenIsAggregate_thenReturnFalse() {
    assertThat(Function.ABS.isAggregate()).isFalse();
    assertThat(Function.UPPER.isAggregate()).isFalse();
    assertThat(Function.LOWER.isAggregate()).isFalse();
    assertThat(Function.CURRENT_DATE.isAggregate()).isFalse();
    assertThat(Function.COALESCE.isAggregate()).isFalse();
  }

  @Test
  void givenVariadicFunction_whenIsVariadic_thenReturnTrue() {
    assertThat(Function.COALESCE.isVariadic()).isTrue();
    assertThat(Function.CONCAT.isVariadic()).isTrue();
  }

  @Test
  void givenNonVariadicFunction_whenIsVariadic_thenReturnFalse() {
    assertThat(Function.ABS.isVariadic()).isFalse();
    assertThat(Function.UPPER.isVariadic()).isFalse();
    assertThat(Function.MOD.isVariadic()).isFalse();
  }

  @Test
  void givenFunctionArgumentCounts_thenVerifyExpectedCounts() {
    // 0-argument functions
    assertThat(Function.CURRENT_DATE.getArgumentCount()).isZero();
    assertThat(Function.CURRENT_TIME.getArgumentCount()).isZero();
    assertThat(Function.CURRENT_TIMESTAMP.getArgumentCount()).isZero();

    // 1-argument functions
    assertThat(Function.ABS.getArgumentCount()).isEqualTo(1);
    assertThat(Function.SQRT.getArgumentCount()).isEqualTo(1);
    assertThat(Function.TRIM.getArgumentCount()).isEqualTo(1);
    assertThat(Function.LTRIM.getArgumentCount()).isEqualTo(1);
    assertThat(Function.RTRIM.getArgumentCount()).isEqualTo(1);
    assertThat(Function.LOWER.getArgumentCount()).isEqualTo(1);
    assertThat(Function.UPPER.getArgumentCount()).isEqualTo(1);
    assertThat(Function.LENGTH.getArgumentCount()).isEqualTo(1);
    assertThat(Function.COUNT.getArgumentCount()).isEqualTo(1);
    assertThat(Function.SUM.getArgumentCount()).isEqualTo(1);
    assertThat(Function.AVG.getArgumentCount()).isEqualTo(1);
    assertThat(Function.MIN.getArgumentCount()).isEqualTo(1);
    assertThat(Function.MAX.getArgumentCount()).isEqualTo(1);

    // 2-argument functions
    assertThat(Function.MOD.getArgumentCount()).isEqualTo(2);
    assertThat(Function.LOCATE.getArgumentCount()).isEqualTo(2);
    assertThat(Function.NULLIF.getArgumentCount()).isEqualTo(2);

    // 3-argument functions
    assertThat(Function.SUBSTRING.getArgumentCount()).isEqualTo(3);
  }
}
