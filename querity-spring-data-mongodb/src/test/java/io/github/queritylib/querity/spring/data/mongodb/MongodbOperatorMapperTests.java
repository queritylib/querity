package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.Operator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MongodbOperatorMapperTests {
  private static final Set<Operator> FIELD_TO_FIELD_SUPPORTED_OPERATORS = Set.of(
      Operator.EQUALS,
      Operator.NOT_EQUALS,
      Operator.GREATER_THAN,
      Operator.GREATER_THAN_EQUALS,
      Operator.LESSER_THAN,
      Operator.LESSER_THAN_EQUALS
  );

  @Test
  void testAllOperatorsSupported() {
    assertThat(Operator.values())
        .allMatch(MongodbOperatorMapper.OPERATOR_CRITERIA_MAP::containsKey);
  }

  @Test
  void testFieldToFieldOperatorsSupported() {
    assertThat(FIELD_TO_FIELD_SUPPORTED_OPERATORS)
        .allMatch(MongodbOperatorMapper.FIELD_TO_FIELD_EXPR_OPERATORS::containsKey);
  }

  @Test
  void testFieldToFieldMapContainsOnlySupportedOperators() {
    assertThat(MongodbOperatorMapper.FIELD_TO_FIELD_EXPR_OPERATORS.keySet())
        .containsExactlyInAnyOrderElementsOf(FIELD_TO_FIELD_SUPPORTED_OPERATORS);
  }
}
