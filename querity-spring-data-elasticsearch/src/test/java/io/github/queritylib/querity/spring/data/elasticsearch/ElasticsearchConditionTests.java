package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.FieldReference;
import io.github.queritylib.querity.api.Operator;
import io.github.queritylib.querity.api.SimpleCondition;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static io.github.queritylib.querity.common.util.ReflectionUtils.findClassWithConstructorArgumentOfType;
import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElasticsearchConditionTests {
  @Test
  void testAllConditionClassesImplemented() {
    Set<Class<? extends Condition>> conditionClasses = findSubclasses(Condition.class);

    Set<Class<? extends ElasticsearchCondition>> implementationClasses = findSubclasses(ElasticsearchCondition.class);

    assertThat(conditionClasses)
        .map(clazz -> findClassWithConstructorArgumentOfType(implementationClasses, clazz))
        .allMatch(Optional::isPresent);
  }

  @Test
  void givenNotSupportedCondition_whenOf_thenThrowIllegalArgumentException() {
    Condition condition = new UnsupportedCondition();
    assertThrows(IllegalArgumentException.class, () -> ElasticsearchCondition.of(condition),
        "Condition class UnsupportedCondition is not supported by the Elasticsearch module");
  }

  @Test
  void givenFieldReferenceWithEquals_whenGetCriteria_thenThrowUnsupportedOperationException() {
    assertFieldReferenceThrowsUnsupportedOperationException(Operator.EQUALS);
  }

  @Test
  void givenFieldReferenceWithNotEquals_whenGetCriteria_thenThrowUnsupportedOperationException() {
    assertFieldReferenceThrowsUnsupportedOperationException(Operator.NOT_EQUALS);
  }

  @Test
  void givenFieldReferenceWithGreaterThan_whenGetCriteria_thenThrowUnsupportedOperationException() {
    assertFieldReferenceThrowsUnsupportedOperationException(Operator.GREATER_THAN);
  }

  @Test
  void givenFieldReferenceWithGreaterThanEquals_whenGetCriteria_thenThrowUnsupportedOperationException() {
    assertFieldReferenceThrowsUnsupportedOperationException(Operator.GREATER_THAN_EQUALS);
  }

  @Test
  void givenFieldReferenceWithLesserThan_whenGetCriteria_thenThrowUnsupportedOperationException() {
    assertFieldReferenceThrowsUnsupportedOperationException(Operator.LESSER_THAN);
  }

  @Test
  void givenFieldReferenceWithLesserThanEquals_whenGetCriteria_thenThrowUnsupportedOperationException() {
    assertFieldReferenceThrowsUnsupportedOperationException(Operator.LESSER_THAN_EQUALS);
  }

  private void assertFieldReferenceThrowsUnsupportedOperationException(Operator operator) {
    SimpleCondition condition = SimpleCondition.builder()
        .propertyName("startDate")
        .operator(operator)
        .value(FieldReference.of("endDate"))
        .build();

    assertThatThrownBy(() -> ElasticsearchOperatorMapper.getCriteria(Object.class, condition, false))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("Field-to-field comparison is not supported in Elasticsearch");
  }

  private static class UnsupportedCondition implements Condition {
  }
}

