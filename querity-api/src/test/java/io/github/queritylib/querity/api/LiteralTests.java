package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LiteralTests {

  @Test
  void givenStringValue_whenOf_thenReturnLiteral() {
    Literal lit = Literal.of("hello");

    assertThat(lit.getValue()).isEqualTo("hello");
  }

  @Test
  void givenIntegerValue_whenOf_thenReturnLiteral() {
    Literal lit = Literal.of(42);

    assertThat(lit.getValue()).isEqualTo(42);
  }

  @Test
  void givenDoubleValue_whenOf_thenReturnLiteral() {
    Literal lit = Literal.of(3.14);

    assertThat(lit.getValue()).isEqualTo(3.14);
  }

  @Test
  void givenBooleanValue_whenOf_thenReturnLiteral() {
    Literal lit = Literal.of(true);

    assertThat(lit.getValue()).isEqualTo(true);
  }

  @Test
  void givenNullValue_whenOf_thenThrowException() {
    assertThatThrownBy(() -> Literal.of(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("cannot be null");
  }

  @Test
  void givenUnsupportedType_whenOf_thenThrowException() {
    assertThatThrownBy(() -> Literal.of(new Object()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("must be String, Number, or Boolean");
  }

  @Test
  void givenStringLiteral_whenToString_thenReturnQuotedValue() {
    Literal lit = Literal.of("test");

    assertThat(lit.toString()).isEqualTo("\"test\"");
  }

  @Test
  void givenNumberLiteral_whenToString_thenReturnValue() {
    Literal lit = Literal.of(123);

    assertThat(lit.toString()).isEqualTo("123");
  }

  @Test
  void givenBooleanLiteral_whenToString_thenReturnValue() {
    Literal lit = Literal.of(false);

    assertThat(lit.toString()).isEqualTo("false");
  }

  @Test
  void givenTwoEqualLiterals_whenEquals_thenReturnTrue() {
    Literal lit1 = Literal.of("value");
    Literal lit2 = Literal.of("value");

    assertThat(lit1).isEqualTo(lit2);
    assertThat(lit1.hashCode()).isEqualTo(lit2.hashCode());
  }

  @Test
  void givenDifferentLiterals_whenEquals_thenReturnFalse() {
    Literal lit1 = Literal.of("value1");
    Literal lit2 = Literal.of("value2");

    assertThat(lit1).isNotEqualTo(lit2);
  }

  @Test
  void givenLiteral_whenIsFunctionArgument_thenReturnTrue() {
    Literal lit = Literal.of("test");

    assertThat(lit).isInstanceOf(FunctionArgument.class);
  }

  @Test
  void givenLongValue_whenOf_thenReturnLiteral() {
    Literal lit = Literal.of(123456789L);

    assertThat(lit.getValue()).isEqualTo(123456789L);
  }

  @Test
  void givenFloatValue_whenOf_thenReturnLiteral() {
    Literal lit = Literal.of(1.5f);

    assertThat(lit.getValue()).isEqualTo(1.5f);
  }
}
