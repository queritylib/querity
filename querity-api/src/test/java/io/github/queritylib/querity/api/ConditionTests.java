package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Operator.*;
import static io.github.queritylib.querity.api.Querity.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Note: FunctionCall and PropertyReference are used in left expression tests

class ConditionTests {
  @Test
  void givenNoPropertyNameOrLeftExpression_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    SimpleCondition.SimpleConditionBuilder builder = SimpleCondition.builder();
    assertThrows(IllegalArgumentException.class,
        builder::build,
        "Either propertyName or leftExpression must be set");
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

  // Field Reference Tests

  @Test
  void givenFieldReference_whenBuildSimpleCondition_thenReturnConditionWithFieldReference() {
    FieldReference fieldRef = field("endDate");
    SimpleCondition condition = filterByField("startDate", LESSER_THAN, fieldRef);

    assertThat(condition.getPropertyName()).isEqualTo("startDate");
    assertThat(condition.getOperator()).isEqualTo(LESSER_THAN);
    assertThat(condition.isFieldReference()).isTrue();
    assertThat(condition.getFieldReference()).isEqualTo(fieldRef);
    assertThat(condition.getValue()).isEqualTo(fieldRef);
  }

  @Test
  void givenFieldReferenceWithEquals_whenBuildSimpleCondition_thenReturnConditionWithFieldReference() {
    SimpleCondition condition = filterByField("field1", EQUALS, field("field2"));

    assertThat(condition.isFieldReference()).isTrue();
    assertThat(condition.getFieldReference().getFieldName()).isEqualTo("field2");
  }

  @Test
  void givenFieldReferenceWithNotEquals_whenBuildSimpleCondition_thenReturnConditionWithFieldReference() {
    SimpleCondition condition = filterByField("field1", NOT_EQUALS, field("field2"));

    assertThat(condition.isFieldReference()).isTrue();
    assertThat(condition.getOperator()).isEqualTo(NOT_EQUALS);
  }

  @Test
  void givenFieldReferenceWithGreaterThan_whenBuildSimpleCondition_thenReturnConditionWithFieldReference() {
    SimpleCondition condition = filterByField("price", GREATER_THAN, field("minPrice"));

    assertThat(condition.isFieldReference()).isTrue();
    assertThat(condition.getOperator()).isEqualTo(GREATER_THAN);
  }

  @Test
  void givenFieldReferenceWithGreaterThanEquals_whenBuildSimpleCondition_thenReturnConditionWithFieldReference() {
    SimpleCondition condition = filterByField("price", GREATER_THAN_EQUALS, field("minPrice"));

    assertThat(condition.isFieldReference()).isTrue();
    assertThat(condition.getOperator()).isEqualTo(GREATER_THAN_EQUALS);
  }

  @Test
  void givenFieldReferenceWithLesserThan_whenBuildSimpleCondition_thenReturnConditionWithFieldReference() {
    SimpleCondition condition = filterByField("price", LESSER_THAN, field("maxPrice"));

    assertThat(condition.isFieldReference()).isTrue();
    assertThat(condition.getOperator()).isEqualTo(LESSER_THAN);
  }

  @Test
  void givenFieldReferenceWithLesserThanEquals_whenBuildSimpleCondition_thenReturnConditionWithFieldReference() {
    SimpleCondition condition = filterByField("price", LESSER_THAN_EQUALS, field("maxPrice"));

    assertThat(condition.isFieldReference()).isTrue();
    assertThat(condition.getOperator()).isEqualTo(LESSER_THAN_EQUALS);
  }

  @Test
  void givenFieldReferenceWithStartsWith_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    FieldReference fieldRef = field("otherField");
    assertThrows(IllegalArgumentException.class,
        () -> buildConditionWithFieldReference(STARTS_WITH, fieldRef));
  }

  @Test
  void givenFieldReferenceWithEndsWith_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    FieldReference fieldRef = field("otherField");
    assertThrows(IllegalArgumentException.class,
        () -> buildConditionWithFieldReference(ENDS_WITH, fieldRef));
  }

  @Test
  void givenFieldReferenceWithContains_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    FieldReference fieldRef = field("otherField");
    assertThrows(IllegalArgumentException.class,
        () -> buildConditionWithFieldReference(CONTAINS, fieldRef));
  }

  @Test
  void givenFieldReferenceWithIn_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    FieldReference fieldRef = field("otherField");
    assertThrows(IllegalArgumentException.class,
        () -> buildConditionWithFieldReference(IN, fieldRef));
  }

  @Test
  void givenFieldReferenceWithNotIn_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    FieldReference fieldRef = field("otherField");
    assertThrows(IllegalArgumentException.class,
        () -> buildConditionWithFieldReference(NOT_IN, fieldRef));
  }

  @Test
  void givenNonFieldReferenceValue_whenIsFieldReference_thenReturnFalse() {
    SimpleCondition condition = filterBy("lastName", "Skywalker");

    assertThat(condition.isFieldReference()).isFalse();
    assertThat(condition.getFieldReference()).isNull();
  }

  @Test
  void givenFieldReference_whenEquals_thenCompareByFieldName() {
    FieldReference ref1 = field("endDate");
    FieldReference ref2 = field("endDate");
    FieldReference ref3 = field("startDate");

    assertThat(ref1)
        .isEqualTo(ref2)
        .isNotEqualTo(ref3);
  }

  @Test
  void givenFieldReference_whenToString_thenReturnReadableString() {
    FieldReference ref = field("endDate");
    assertThat(ref.toString()).contains("endDate");
  }

  @Test
  void givenNullFieldName_whenCreateFieldReference_thenThrowNullPointerException() {
    assertThrows(NullPointerException.class, () -> field(null));
  }

  // Left Expression Tests

  @Test
  void givenPropertyName_whenHasLeftExpression_thenReturnFalse() {
    SimpleCondition condition = filterBy("lastName", "Skywalker");

    assertThat(condition.hasLeftExpression()).isFalse();
  }

  @Test
  void givenLeftExpression_whenHasLeftExpression_thenReturnTrue() {
    SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(PropertyReference.of("lastName"))
        .operator(EQUALS)
        .value("Skywalker")
        .build();

    assertThat(condition.hasLeftExpression()).isTrue();
  }

  @Test
  void givenPropertyName_whenGetEffectiveLeftExpression_thenReturnPropertyReference() {
    SimpleCondition condition = filterBy("lastName", "Skywalker");

    PropertyExpression effectiveExpr = condition.getEffectiveLeftExpression();

    assertThat(effectiveExpr).isInstanceOf(PropertyReference.class);
    assertThat(((PropertyReference) effectiveExpr).getPropertyName()).isEqualTo("lastName");
  }

  @Test
  void givenLeftExpression_whenGetEffectiveLeftExpression_thenReturnLeftExpression() {
    PropertyReference propRef = PropertyReference.of("firstName");
    SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(propRef)
        .operator(EQUALS)
        .value("Luke")
        .build();

    PropertyExpression effectiveExpr = condition.getEffectiveLeftExpression();

    assertThat(effectiveExpr).isSameAs(propRef);
  }

  @Test
  void givenFunctionCallAsLeftExpression_whenGetEffectiveLeftExpression_thenReturnFunctionCall() {
    FunctionCall funcCall = FunctionCall.of(Function.UPPER, PropertyReference.of("lastName"));
    SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(funcCall)
        .operator(EQUALS)
        .value("SKYWALKER")
        .build();

    PropertyExpression effectiveExpr = condition.getEffectiveLeftExpression();

    assertThat(effectiveExpr).isSameAs(funcCall);
  }

  @Test
  void givenBothPropertyNameAndLeftExpression_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        ConditionTests::buildConditionWithPropertyNameAndLeftExpression,
        "Cannot set both propertyName and leftExpression");
  }

  @Test
  void givenFieldReferenceWithIsNull_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    FieldReference fieldRef = field("otherField");
    assertThrows(IllegalArgumentException.class,
        () -> buildConditionWithFieldReference(IS_NULL, fieldRef));
  }

  @Test
  void givenFieldReferenceWithIsNotNull_whenBuildSimpleCondition_thenThrowIllegalArgumentException() {
    FieldReference fieldRef = field("otherField");
    assertThrows(IllegalArgumentException.class,
        () -> buildConditionWithFieldReference(IS_NOT_NULL, fieldRef));
  }

  @Test
  void givenSimpleCondition_whenToBuilder_thenReturnBuilderWithSameValues() {
    SimpleCondition original = filterBy("lastName", EQUALS, "Skywalker");

    SimpleCondition copy = original.toBuilder().build();

    assertThat(copy.getPropertyName()).isEqualTo(original.getPropertyName());
    assertThat(copy.getOperator()).isEqualTo(original.getOperator());
    assertThat(copy.getValue()).isEqualTo(original.getValue());
  }

  @Test
  void givenSimpleCondition_whenEquals_thenReturnTrueForEqualConditions() {
    SimpleCondition condition1 = filterBy("lastName", EQUALS, "Skywalker");
    SimpleCondition condition2 = filterBy("lastName", EQUALS, "Skywalker");

    assertThat(condition1).isEqualTo(condition2);
  }

  @Test
  void givenSimpleCondition_whenHashCode_thenReturnSameHashCodeForEqualConditions() {
    SimpleCondition condition1 = filterBy("lastName", EQUALS, "Skywalker");
    SimpleCondition condition2 = filterBy("lastName", EQUALS, "Skywalker");

    assertThat(condition1).hasSameHashCodeAs(condition2);
  }

  @Test
  void givenSimpleCondition_whenToString_thenReturnReadableString() {
    SimpleCondition condition = filterBy("lastName", EQUALS, "Skywalker");

    assertThat(condition.toString())
        .contains("lastName")
        .contains("EQUALS")
        .contains("Skywalker");
  }

  @Test
  void givenLeftExpressionCondition_whenGetPropertyName_thenReturnNull() {
    SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(PropertyReference.of("name"))
        .operator(EQUALS)
        .value("test")
        .build();

    assertThat(condition.getPropertyName()).isNull();
    assertThat(condition.getLeftExpression()).isNotNull();
  }

  private static void buildConditionWithFieldReference(Operator operator, FieldReference fieldRef) {
    SimpleCondition.builder()
        .propertyName("someField")
        .operator(operator)
        .value(fieldRef)
        .build();
  }

  private static void buildConditionWithPropertyNameAndLeftExpression() {
    SimpleCondition.builder()
        .propertyName("lastName")
        .leftExpression(PropertyReference.of("firstName"))
        .operator(EQUALS)
        .value("test")
        .build();
  }
}
