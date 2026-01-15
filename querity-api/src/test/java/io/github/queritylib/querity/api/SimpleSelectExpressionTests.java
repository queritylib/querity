package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimpleSelectExpressionTests {

  @Test
  void givenPropertyNames_whenOf_thenReturnSimpleSelectWithPropertyNames() {
    SimpleSelect select = SimpleSelect.of("id", "name", "email");

    assertThat(select.getPropertyNames()).containsExactly("id", "name", "email");
    assertThat(select.hasExpressions()).isFalse();
  }

  @Test
  void givenExpressions_whenOfExpressions_thenReturnSimpleSelectWithExpressions() {
    PropertyReference prop1 = PropertyReference.of("id");
    FunctionCall func = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));

    SimpleSelect select = SimpleSelect.ofExpressions(prop1, func);

    assertThat(select.hasExpressions()).isTrue();
    assertThat(select.getExpressions()).containsExactly(prop1, func);
  }

  @Test
  void givenPropertyNames_whenGetEffectiveExpressions_thenReturnPropertyReferences() {
    SimpleSelect select = SimpleSelect.of("firstName", "lastName");

    List<PropertyExpression> expressions = select.getEffectiveExpressions();

    assertThat(expressions).hasSize(2);
    assertThat(expressions.get(0)).isInstanceOf(PropertyReference.class);
    assertThat(((PropertyReference) expressions.get(0)).getPropertyName()).isEqualTo("firstName");
    assertThat(expressions.get(1)).isInstanceOf(PropertyReference.class);
    assertThat(((PropertyReference) expressions.get(1)).getPropertyName()).isEqualTo("lastName");
  }

  @Test
  void givenExpressions_whenGetEffectiveExpressions_thenReturnSameExpressions() {
    PropertyReference prop = PropertyReference.of("id");
    FunctionCall func = FunctionCall.of(Function.LENGTH, PropertyReference.of("name"));
    SimpleSelect select = SimpleSelect.ofExpressions(prop, func);

    List<PropertyExpression> expressions = select.getEffectiveExpressions();

    assertThat(expressions).containsExactly(prop, func);
  }

  @Test
  void givenExpressionsWithAlias_whenGetAliasNames_thenReturnAliasesOrPropertyNames() {
    FunctionCall func1 = FunctionCall.of(Function.UPPER, PropertyReference.of("name")).as("upperName");
    FunctionCall func2 = FunctionCall.of(Function.LENGTH, PropertyReference.of("desc")).as("descLen");
    PropertyReference prop = PropertyReference.of("id");

    SimpleSelect select = SimpleSelect.ofExpressions(func1, func2, prop);

    List<String> aliases = select.getAliasNames();

    // Aliases for functions with alias, property name for PropertyReference
    assertThat(aliases).containsExactly("upperName", "descLen", "id");
  }

  @Test
  void givenPropertyNames_whenGetAliasNames_thenReturnPropertyNames() {
    SimpleSelect select = SimpleSelect.of("id", "name");

    List<String> aliases = select.getAliasNames();

    // Property names are returned as aliases
    assertThat(aliases).containsExactly("id", "name");
  }

  @Test
  void givenPropertyReferenceWithAlias_whenGetAliasNames_thenReturnAlias() {
    PropertyReference prop = PropertyReference.of("name").as("displayName");
    SimpleSelect select = SimpleSelect.ofExpressions(prop);

    List<String> aliases = select.getAliasNames();

    assertThat(aliases).containsExactly("displayName");
  }

  @Test
  void givenEmptySelect_whenOf_thenThrowsIllegalArgumentException() {
    assertThatThrownBy(() -> SimpleSelect.of())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Either propertyNames or expressions must be set");
  }

  @Test
  void givenEmptyExpressions_whenOfExpressions_thenThrowsIllegalArgumentException() {
    assertThatThrownBy(() -> SimpleSelect.ofExpressions())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Either propertyNames or expressions must be set");
  }

  @Test
  void givenSimpleSelect_whenBuilder_thenBuildCorrectly() {
    SimpleSelect select = SimpleSelect.builder()
        .propertyNames(List.of("id", "name"))
        .build();

    assertThat(select.getPropertyNames()).containsExactly("id", "name");
  }

  @Test
  void givenSimpleSelectWithExpressions_whenBuilder_thenBuildCorrectly() {
    List<PropertyExpression> exprs = List.of(
        PropertyReference.of("id"),
        FunctionCall.of(Function.UPPER, PropertyReference.of("name"))
    );
    SimpleSelect select = SimpleSelect.builder()
        .expressions(exprs)
        .build();

    assertThat(select.hasExpressions()).isTrue();
    assertThat(select.getExpressions()).hasSize(2);
  }

  @Test
  void givenTwoEqualSimpleSelects_whenEquals_thenReturnTrue() {
    SimpleSelect select1 = SimpleSelect.of("id", "name");
    SimpleSelect select2 = SimpleSelect.of("id", "name");

    assertThat(select1)
        .isEqualTo(select2)
        .hasSameHashCodeAs(select2);
  }

  @Test
  void givenDifferentSimpleSelects_whenEquals_thenReturnFalse() {
    SimpleSelect select1 = SimpleSelect.of("id", "name");
    SimpleSelect select2 = SimpleSelect.of("id", "email");

    assertThat(select1).isNotEqualTo(select2);
  }

  @Test
  void givenSimpleSelect_whenToString_thenReturnReadableOutput() {
    SimpleSelect select = SimpleSelect.of("id", "firstName", "lastName");

    String str = select.toString();

    assertThat(str)
        .contains("id")
        .contains("firstName")
        .contains("lastName");
  }

  @Test
  void givenMixedExpressionsAndPropertyNames_whenBuild_thenCombinesBoth() {
    FunctionCall func = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
    
    SimpleSelect select = SimpleSelect.builder()
        .propertyNames(List.of("id"))
        .expressions(List.of(func))
        .build();
    
    assertThat(select.hasPropertyNames()).isTrue();
    assertThat(select.hasExpressions()).isTrue();
    assertThat(select.getPropertyNames()).containsExactly("id");
    assertThat(select.getExpressions()).hasSize(1);
    
    // Effective expressions should contain both: propertyNames first, then expressions
    List<PropertyExpression> effective = select.getEffectiveExpressions();
    assertThat(effective).hasSize(2);
    assertThat(effective.get(0)).isInstanceOf(PropertyReference.class);
    assertThat(effective.get(1)).isInstanceOf(FunctionCall.class);
    
    // Alias names should also contain both
    List<String> aliases = select.getAliasNames();
    assertThat(aliases).hasSize(2);
    assertThat(aliases.get(0)).isEqualTo("id");
  }

  @Test
  void givenEffectiveExpressionsListModified_whenGetEffectiveExpressions_thenListIsImmutable() {
    PropertyReference prop = PropertyReference.of("id");
    FunctionCall func = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
    SimpleSelect select = SimpleSelect.ofExpressions(prop, func);

    List<PropertyExpression> expressions = select.getEffectiveExpressions();

    assertThatThrownBy(() -> expressions.add(PropertyReference.of("newProp")))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void givenPropertyNames_whenGetEffectiveExpressions_thenListIsImmutable() {
    SimpleSelect select = SimpleSelect.of("id", "name");

    List<PropertyExpression> expressions = select.getEffectiveExpressions();

    assertThatThrownBy(() -> expressions.add(PropertyReference.of("newProp")))
        .isInstanceOf(UnsupportedOperationException.class);
  }
}
