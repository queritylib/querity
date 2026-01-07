package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.Operator;
import io.github.queritylib.querity.api.PropertyReference;
import io.github.queritylib.querity.api.SimpleCondition;
import lombok.Data;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.elasticsearch.core.query.Criteria;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElasticsearchOperatorMapperTests {

  private static final Set<Operator> ALL_OPERATORS = Set.of(Operator.values());

  @Test
  void testAllOperatorsSupported() {
    assertThat(ElasticsearchOperatorMapper.OPERATOR_CRITERIA_MAP.keySet())
        .containsAll(ALL_OPERATORS);
  }

  @Nested
  class GetCriteriaTests {

    @Test
    void givenEqualsCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.EQUALS)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenNotEqualsCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.NOT_EQUALS)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenStartsWithCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.STARTS_WITH)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenEndsWithCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.ENDS_WITH)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenContainsCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.CONTAINS)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenGreaterThanCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("age")
          .operator(Operator.GREATER_THAN)
          .value(18)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenGreaterThanEqualsCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("age")
          .operator(Operator.GREATER_THAN_EQUALS)
          .value(18)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenLesserThanCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("age")
          .operator(Operator.LESSER_THAN)
          .value(65)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenLesserThanEqualsCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("age")
          .operator(Operator.LESSER_THAN_EQUALS)
          .value(65)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenIsNullCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("middleName")
          .operator(Operator.IS_NULL)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenIsNotNullCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("middleName")
          .operator(Operator.IS_NOT_NULL)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenInCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("status")
          .operator(Operator.IN)
          .value(new String[]{"ACTIVE", "PENDING"})
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenNotInCondition_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("status")
          .operator(Operator.NOT_IN)
          .value(new String[]{"DELETED", "ARCHIVED"})
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenInConditionWithNonArray_whenGetCriteria_thenThrowIllegalArgumentException() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("status")
          .operator(Operator.IN)
          .value("ACTIVE")
          .build();

      assertThatThrownBy(() -> ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Value must be an array");
    }

    @Test
    void givenNotInConditionWithNonArray_whenGetCriteria_thenThrowIllegalArgumentException() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("status")
          .operator(Operator.NOT_IN)
          .value("DELETED")
          .build();

      assertThatThrownBy(() -> ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Value must be an array");
    }
  }

  @Nested
  class NegatedCriteriaTests {

    @Test
    void givenEqualsConditionNegated_whenGetCriteria_thenReturnNegatedCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.EQUALS)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenNotEqualsConditionNegated_whenGetCriteria_thenReturnNegatedCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.NOT_EQUALS)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenStartsWithConditionNegated_whenGetCriteria_thenReturnNegatedCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.STARTS_WITH)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenEndsWithConditionNegated_whenGetCriteria_thenReturnNegatedCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.ENDS_WITH)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenContainsConditionNegated_whenGetCriteria_thenReturnNegatedCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("name")
          .operator(Operator.CONTAINS)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenGreaterThanConditionNegated_whenGetCriteria_thenReturnLesserThanEqualsCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("age")
          .operator(Operator.GREATER_THAN)
          .value(18)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenGreaterThanEqualsConditionNegated_whenGetCriteria_thenReturnLesserThanCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("age")
          .operator(Operator.GREATER_THAN_EQUALS)
          .value(18)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenLesserThanConditionNegated_whenGetCriteria_thenReturnGreaterThanEqualsCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("age")
          .operator(Operator.LESSER_THAN)
          .value(65)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenLesserThanEqualsConditionNegated_whenGetCriteria_thenReturnGreaterThanCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("age")
          .operator(Operator.LESSER_THAN_EQUALS)
          .value(65)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenIsNullConditionNegated_whenGetCriteria_thenReturnExistsCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("middleName")
          .operator(Operator.IS_NULL)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenIsNotNullConditionNegated_whenGetCriteria_thenReturnNotExistsCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("middleName")
          .operator(Operator.IS_NOT_NULL)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenInConditionNegated_whenGetCriteria_thenReturnNotInCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("status")
          .operator(Operator.IN)
          .value(new String[]{"ACTIVE", "PENDING"})
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }

    @Test
    void givenNotInConditionNegated_whenGetCriteria_thenReturnInCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("status")
          .operator(Operator.NOT_IN)
          .value(new String[]{"DELETED", "ARCHIVED"})
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, true);

      assertThat(result).isNotNull();
    }
  }

  @Nested
  class PropertyExpressionTests {

    @Test
    void givenPropertyReference_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .leftExpression(PropertyReference.of("name"))
          .operator(Operator.EQUALS)
          .value("test")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenNestedPropertyReference_whenGetCriteria_thenReturnCriteria() {
      SimpleCondition condition = SimpleCondition.builder()
          .leftExpression(PropertyReference.of("address.city"))
          .operator(Operator.EQUALS)
          .value("Rome")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @Test
    void givenFunctionExpression_whenGetCriteria_thenThrowUnsupportedOperationException() {
      FunctionCall functionCall = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
      SimpleCondition condition = SimpleCondition.builder()
          .leftExpression(functionCall)
          .operator(Operator.EQUALS)
          .value("TEST")
          .build();

      assertThatThrownBy(() -> ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false))
          .isInstanceOf(UnsupportedOperationException.class)
          .hasMessageContaining("Function UPPER is not supported in Elasticsearch");
    }
  }

  @Nested
  class AllOperatorsParameterizedTests {

    @ParameterizedTest
    @EnumSource(value = Operator.class, names = {"EQUALS", "NOT_EQUALS", "STARTS_WITH", "ENDS_WITH",
        "CONTAINS", "GREATER_THAN", "GREATER_THAN_EQUALS", "LESSER_THAN", "LESSER_THAN_EQUALS"})
    void givenSingleValueOperator_whenGetCriteria_thenReturnCriteria(Operator operator) {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("field")
          .operator(operator)
          .value("testValue")
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(value = Operator.class, names = {"IS_NULL", "IS_NOT_NULL"})
    void givenNullaryOperator_whenGetCriteria_thenReturnCriteria(Operator operator) {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("field")
          .operator(operator)
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(value = Operator.class, names = {"IN", "NOT_IN"})
    void givenArrayOperator_whenGetCriteria_thenReturnCriteria(Operator operator) {
      SimpleCondition condition = SimpleCondition.builder()
          .propertyName("field")
          .operator(operator)
          .value(new String[]{"val1", "val2"})
          .build();

      Criteria result = ElasticsearchOperatorMapper.getCriteria(TestEntity.class, condition, false);

      assertThat(result).isNotNull();
    }
  }

  @Data
  private static class TestEntity {
    private String name;
    private String middleName;
    private Integer age;
    private String status;
    private String field;
    private Address address;
  }

  @Data
  private static class Address {
    private String city;
    private String street;
  }
}
