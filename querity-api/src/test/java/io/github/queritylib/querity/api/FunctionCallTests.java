package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FunctionCallTests {

  @Test
  void givenFunctionOnly_whenBuild_thenReturnFunctionCallWithEmptyArguments() {
    FunctionCall fc = FunctionCall.of(Function.CURRENT_DATE);

    assertThat(fc.getFunction()).isEqualTo(Function.CURRENT_DATE);
    assertThat(fc.getArguments()).isEmpty();
    assertThat(fc.getAlias()).isNull();
  }

  @Test
  void givenFunctionWithArguments_whenBuild_thenReturnFunctionCallWithArguments() {
    PropertyReference prop = PropertyReference.of("lastName");
    FunctionCall fc = FunctionCall.of(Function.UPPER, prop);

    assertThat(fc.getFunction()).isEqualTo(Function.UPPER);
    assertThat(fc.getArguments()).containsExactly(prop);
  }

  @Test
  void givenFunctionWithMultipleArguments_whenBuild_thenReturnFunctionCallWithAllArguments() {
    PropertyReference prop1 = PropertyReference.of("firstName");
    PropertyReference prop2 = PropertyReference.of("lastName");
    FunctionCall fc = FunctionCall.of(Function.CONCAT, prop1, Literal.of(" "), prop2);

    assertThat(fc.getFunction()).isEqualTo(Function.CONCAT);
    assertThat(fc.getArguments()).hasSize(3);
    assertThat(fc.getArguments().get(0)).isEqualTo(prop1);
    assertThat(fc.getArguments().get(1)).isEqualTo(Literal.of(" "));
    assertThat(fc.getArguments().get(2)).isEqualTo(prop2);
  }

  @Test
  void givenFunctionCall_whenAs_thenReturnFunctionCallWithAlias() {
    FunctionCall fc = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
    FunctionCall aliased = fc.as("upperName");

    assertThat(aliased.getFunction()).isEqualTo(Function.UPPER);
    assertThat(aliased.getAlias()).isEqualTo("upperName");
    // Original should be unchanged
    assertThat(fc.getAlias()).isNull();
  }

  @Test
  void givenFunctionCall_whenToExpressionString_thenReturnReadableString() {
    FunctionCall fc = FunctionCall.of(Function.UPPER, PropertyReference.of("lastName"));

    String result = fc.toExpressionString();

    assertThat(result).isEqualTo("UPPER(lastName)");
  }

  @Test
  void givenNestedFunctionCall_whenToExpressionString_thenReturnNestedString() {
    FunctionCall inner = FunctionCall.of(Function.TRIM, PropertyReference.of("name"));
    FunctionCall outer = FunctionCall.of(Function.UPPER, inner);

    String result = outer.toExpressionString();

    assertThat(result).isEqualTo("UPPER(TRIM(name))");
  }

  @Test
  void givenFunctionCallWithAlias_whenToExpressionString_thenIncludeAlias() {
    FunctionCall fc = FunctionCall.of(Function.LENGTH, PropertyReference.of("name")).as("nameLength");

    String result = fc.toExpressionString();

    assertThat(result).isEqualTo("LENGTH(name) AS nameLength");
  }

  @Test
  void givenFunctionCallWithLiteralArguments_whenToExpressionString_thenReturnCorrectString() {
    FunctionCall fc = FunctionCall.of(Function.SUBSTRING, PropertyReference.of("name"), Literal.of(1), Literal.of(5));

    String result = fc.toExpressionString();

    assertThat(result).isEqualTo("SUBSTRING(name, 1, 5)");
  }

  @Test
  void givenTwoEqualFunctionCalls_whenEquals_thenReturnTrue() {
    FunctionCall fc1 = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
    FunctionCall fc2 = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));

    assertThat(fc1)
        .isEqualTo(fc2)
        .hasSameHashCodeAs(fc2);
  }

  @Test
  void givenDifferentFunctionCalls_whenEquals_thenReturnFalse() {
    FunctionCall fc1 = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
    FunctionCall fc2 = FunctionCall.of(Function.LOWER, PropertyReference.of("name"));

    assertThat(fc1).isNotEqualTo(fc2);
  }

  @Test
  void givenFunctionCallWithDifferentArguments_whenEquals_thenReturnFalse() {
    FunctionCall fc1 = FunctionCall.of(Function.UPPER, PropertyReference.of("firstName"));
    FunctionCall fc2 = FunctionCall.of(Function.UPPER, PropertyReference.of("lastName"));

    assertThat(fc1).isNotEqualTo(fc2);
  }

  @Test
  void givenFunctionCall_whenBuilder_thenBuildCorrectly() {
    FunctionCall fc = FunctionCall.builder()
        .function(Function.LENGTH)
        .arguments(List.of(PropertyReference.of("description")))
        .alias("descLength")
        .build();

    assertThat(fc.getFunction()).isEqualTo(Function.LENGTH);
    assertThat(fc.getArguments()).hasSize(1);
    assertThat(fc.getAlias()).isEqualTo("descLength");
  }

  @Test
  void givenNullFunction_whenBuild_thenThrowException() {
    assertThrows(NullPointerException.class, () -> FunctionCall.of(null));
  }

  @Test
  void givenNullaryFunctionWithArguments_whenBuild_thenThrowException() {
    PropertyReference ref = PropertyReference.of("date");
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> FunctionCall.of(Function.CURRENT_DATE, ref));

    assertThat(exception.getMessage()).contains("does not accept any arguments");
  }

  @Test
  void givenFunctionWithWrongArgumentCount_whenBuild_thenThrowException() {
    // MOD requires exactly 2 arguments
    PropertyReference ref = PropertyReference.of("value");
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> FunctionCall.of(Function.MOD, ref));

    assertThat(exception.getMessage()).contains("requires 2 argument(s)");
  }

  @Test
  void givenVariadicFunctionWithInsufficientArguments_whenBuild_thenThrowException() {
    // COALESCE requires at least 1 argument
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> FunctionCall.of(Function.COALESCE));

    assertThat(exception.getMessage()).contains("requires at least 1 argument(s)");
  }

  @Test
  void givenConcatWithOneArgument_whenBuild_thenThrowException() {
    // CONCAT requires at least 2 arguments
    PropertyReference ref = PropertyReference.of("name");
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> FunctionCall.of(Function.CONCAT, ref));

    assertThat(exception.getMessage()).contains("requires at least 2 argument(s)");
  }

  @Test
  void givenNullaryFunction_whenToExpressionString_thenReturnFunctionNameOnly() {
    FunctionCall fc = FunctionCall.of(Function.CURRENT_DATE);

    String result = fc.toExpressionString();

    assertThat(result).isEqualTo("CURRENT_DATE");
  }

  @Test
  void givenFunctionCallWithEmptyAlias_whenHasAlias_thenReturnFalse() {
    FunctionCall fc = FunctionCall.builder()
        .function(Function.UPPER)
        .argument(PropertyReference.of("name"))
        .alias("")
        .build();

    assertThat(fc.hasAlias()).isFalse();
  }

  @Test
  void givenFunctionCallWithStringLiteral_whenToExpressionString_thenQuoteString() {
    FunctionCall fc = FunctionCall.of(Function.CONCAT, PropertyReference.of("firstName"), Literal.of(" - "), PropertyReference.of("lastName"));

    String result = fc.toExpressionString();

    assertThat(result).isEqualTo("CONCAT(firstName, \" - \", lastName)");
  }

  @Test
  void givenFunctionCall_whenToString_thenReturnReadableOutput() {
    FunctionCall fc = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));

    String str = fc.toString();

    assertThat(str)
        .contains("UPPER")
        .contains("name");
  }
}
