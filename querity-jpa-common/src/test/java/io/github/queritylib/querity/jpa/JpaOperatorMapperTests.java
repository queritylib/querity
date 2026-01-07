package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Operator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JpaOperatorMapperTests {
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
    assertThat(JpaOperatorMapper.OPERATOR_PREDICATE_MAP.keySet())
        .containsAll(Set.of(Operator.values()));
  }

  @Test
  void testFieldToFieldOperatorsSupported() {
    assertThat(JpaOperatorMapper.FIELD_TO_FIELD_PREDICATE_MAP.keySet())
        .containsAll(FIELD_TO_FIELD_SUPPORTED_OPERATORS);
  }

  @Test
  void testFieldToFieldMapContainsOnlySupportedOperators() {
    assertThat(JpaOperatorMapper.FIELD_TO_FIELD_PREDICATE_MAP.keySet())
        .containsExactlyInAnyOrderElementsOf(FIELD_TO_FIELD_SUPPORTED_OPERATORS);
  }
}
