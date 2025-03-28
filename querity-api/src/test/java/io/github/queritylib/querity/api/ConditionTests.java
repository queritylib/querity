package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Operator.EQUALS;
import static io.github.queritylib.querity.api.Operator.IS_NULL;
import static io.github.queritylib.querity.api.Querity.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConditionTests {
  @Test
  void givenNoPropertyName_whenBuildSimpleCondition_thenThrowNullPointerException() {
    SimpleCondition.SimpleConditionBuilder builder = SimpleCondition.builder();
    assertThrows(NullPointerException.class,
        builder::build,
        "propertyName is marked non-null but is null");
  }

  @Test
  void givenNoOperator_whenBuildSimpleCondition_thenReturnEqualsCondition() {
    SimpleCondition condition = filterBy("lastName", "Skywalker");
    assertThat(condition.getPropertyName()).isEqualTo("lastName");
    assertThat(condition.getOperator()).isEqualTo(EQUALS);
    assertThat(condition.getValue()).isEqualTo("Skywalker");
  }

  @Test
  void givenIsNullCondition_whenBuildSimpleCondition_thenReturnIsNullConditionWithoutValue() {
    SimpleCondition condition = getIsNullCondition();
    assertThat(condition.getPropertyName()).isEqualTo("lastName");
    assertThat(condition.getOperator()).isEqualTo(IS_NULL);
    assertThat(condition.getValue()).isNull();
  }

  @Test
  void givenEqualsConditionWithoutValue_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> filterBy("lastName", EQUALS),
        "The operator EQUALS requires 1 value(s)");
  }

  @Test
  void givenIsNullConditionWithValue_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> filterBy("lastName", IS_NULL, "value"),
        "The operator IS_NULL requires 0 value(s)");
  }

  @Test
  void givenSimpleCondition_whenIsEmpty_thenReturnFalse() {
    Condition condition = getEqualsCondition();
    assertThat(condition.isEmpty()).isFalse();
  }

  @Test
  void givenNotEmptyAndConditionsWrapper_whenIsEmpty_thenReturnFalse() {
    LogicConditionsWrapper condition = and(getEqualsCondition());
    assertThat(condition.getConditions()).isNotEmpty();
    assertThat(condition.isEmpty()).isFalse();
  }

  @Test
  void givenEmptyAndConditionsWrapper_whenIsEmpty_thenReturnTrue() {
    LogicConditionsWrapper condition = and();
    assertThat(condition.getConditions()).isEmpty();
    assertThat(condition.isEmpty()).isTrue();
  }

  @Test
  void givenNotEmptyOrConditionsWrapper_whenIsEmpty_thenReturnFalse() {
    LogicConditionsWrapper condition = or(getEqualsCondition());
    assertThat(condition.getConditions()).isNotEmpty();
    assertThat(condition.isEmpty()).isFalse();
  }

  @Test
  void givenEmptyOrConditionsWrapper_whenIsEmpty_thenReturnTrue() {
    LogicConditionsWrapper condition = or();
    assertThat(condition.getConditions()).isEmpty();
    assertThat(condition.isEmpty()).isTrue();
  }

  @Test
  void givenNotConditionWithSimpleCondition_whenIsEmpty_thenReturnFalse() {
    Condition condition = not(getEqualsCondition());
    assertThat(condition.isEmpty()).isFalse();
  }

  @Test
  void givenNotConditionWithNotEmptyConditionsWrapper_whenIsEmpty_thenReturnFalse() {
    Condition condition = not(and(getEqualsCondition()));
    assertThat(condition.isEmpty()).isFalse();
  }

  @Test
  void givenNotConditionWithEmptyConditionsWrapper_whenIsEmpty_thenReturnTrue() {
    Condition condition = not(and());
    assertThat(condition.isEmpty()).isTrue();
  }

  private static SimpleCondition getEqualsCondition() {
    return filterBy("lastName", EQUALS, "Skywalker");
  }

  private static SimpleCondition getIsNullCondition() {
    return filterBy("lastName", IS_NULL);
  }
}
