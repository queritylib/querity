package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

  @Nested
  @ExtendWith(MockitoExtension.class)
  class FunctionExpressionValueConversionTests {
    @Mock
    Root<?> root;
    @Mock
    CriteriaBuilder cb;
    @Mock
    Metamodel metamodel;
    @Mock
    Expression<Object> leftExpression;

    private MockedStatic<JpaFunctionMapper> jpaFunctionMapperMock;

    @BeforeEach
    void setUp() {
      jpaFunctionMapperMock = mockStatic(JpaFunctionMapper.class);
      jpaFunctionMapperMock.when(() -> JpaFunctionMapper.toExpression(any(), any(), any(), any()))
        .thenReturn(leftExpression);
    }

    @AfterEach
    void tearDown() {
      jpaFunctionMapperMock.close();
    }

    @Test
    void givenFunctionExpressionWithKnownJavaType_whenGetPredicate_thenConvertValueToJavaType() {
      doReturn(java.sql.Date.class).when(leftExpression).getJavaType();
      SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(FunctionCall.of(Function.CURRENT_DATE))
        .operator(Operator.EQUALS)
        .value("2026-06-22")
        .build();
      assertThat(getValuePassedToEquals(condition))
        .isEqualTo(java.sql.Date.valueOf(LocalDate.of(2026, 6, 22)));
    }

    @Test
    void givenFunctionExpressionWithoutJavaType_whenGetPredicate_thenConvertValueToPropertyArgumentType() {
      // getJavaType() returns null (e.g. COALESCE), fall back to the first property argument's type
      SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(FunctionCall.of(Function.COALESCE,
          PropertyReference.of("dateValue"), PropertyReference.of("otherDateValue")))
        .operator(Operator.EQUALS)
        .value("2026-06-22")
        .build();
      assertThat(getValuePassedToEquals(condition)).isEqualTo(LocalDate.of(2026, 6, 22));
    }

    @Test
    void givenFunctionExpressionWithNestedFunctionArgument_whenGetPredicate_thenResolvePropertyTypeRecursively() {
      SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(FunctionCall.of(Function.COALESCE,
          FunctionCall.of(Function.ABS, PropertyReference.of("integerValue")), Literal.of(0)))
        .operator(Operator.EQUALS)
        .value("42")
        .build();
      assertThat(getValuePassedToEquals(condition)).isEqualTo(42L);
    }

    @Test
    void givenFunctionExpressionWhereFirstArgumentTypeIsUnresolvable_whenGetPredicate_thenFallbackToNextPropertyArgument() {
      SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(FunctionCall.of(Function.COALESCE,
          FunctionCall.of(Function.CONCAT, Literal.of("a"), Literal.of("b")),
          PropertyReference.of("dateValue")))
        .operator(Operator.EQUALS)
        .value("2026-06-22")
        .build();
      assertThat(getValuePassedToEquals(condition)).isEqualTo(LocalDate.of(2026, 6, 22));
    }

    @Test
    void givenFunctionExpressionWithoutResolvableType_whenGetPredicate_thenKeepRawValue() {
      SimpleCondition condition = SimpleCondition.builder()
        .leftExpression(FunctionCall.of(Function.CONCAT, Literal.of("Hello"), Literal.of("World")))
        .operator(Operator.EQUALS)
        .value("HelloWorld")
        .build();
      assertThat(getValuePassedToEquals(condition)).isSameAs("HelloWorld");
    }

    private Object getValuePassedToEquals(SimpleCondition condition) {
      JpaOperatorMapper.getPredicate(MyEntity.class, condition, metamodel, root, cb);
      ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
      verify(cb).equal(any(), valueCaptor.capture());
      return valueCaptor.getValue();
    }

    private static class MyEntity {
      private LocalDate dateValue;
      private LocalDate otherDateValue;
      private Integer integerValue;
    }
  }
}
