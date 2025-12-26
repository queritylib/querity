package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.Literal;
import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.PropertyReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaFunctionMapperTests {

  @Test
  void givenAllFunctions_whenCheckFunctionMap_thenAllFunctionsSupported() {
    assertThat(Function.values())
        .allMatch(JpaFunctionMapper.FUNCTION_MAP::containsKey,
            "All functions should be mapped in JpaFunctionMapper");
  }

  @Test
  void givenFunctionMap_whenCheckSize_thenContainsAllFunctions() {
    assertThat(JpaFunctionMapper.FUNCTION_MAP).hasSize(Function.values().length);
  }

  @Test
  void givenFunctionMapValues_whenCheck_thenAllNotNull() {
    assertThat(JpaFunctionMapper.FUNCTION_MAP.values())
        .noneMatch(java.util.Objects::isNull);
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class ToExpressionTests {
    @Mock Root<?> root;
    @Mock CriteriaBuilder cb;
    @Mock Metamodel metamodel;
    @Mock Path<Object> path;

    private MockedStatic<JpaPropertyUtils> jpaPropertyUtilsMock;

    @BeforeEach
    void setUp() {
      jpaPropertyUtilsMock = mockStatic(JpaPropertyUtils.class);
      jpaPropertyUtilsMock.when(() -> JpaPropertyUtils.getPath(any(), anyString(), any()))
          .thenReturn(path);
    }

    @AfterEach
    void tearDown() {
      jpaPropertyUtilsMock.close();
    }

    @Test
    void givenPropertyReference_whenToExpression_thenReturnPath() {
      PropertyReference ref = PropertyReference.of("name");
      Expression<?> result = JpaFunctionMapper.toExpression(ref, root, cb, metamodel);
      assertThat(result).isEqualTo(path);
    }

    @Test
    void givenUnsupportedExpressionType_whenToExpression_thenThrowException() {
      PropertyExpression unsupported = () -> "unsupported";
      assertThatThrownBy(() -> JpaFunctionMapper.toExpression(unsupported, root, cb, metamodel))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unsupported expression type");
    }
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class ArithmeticFunctionTests {
    @Mock Root<?> root;
    @Mock CriteriaBuilder cb;
    @Mock Metamodel metamodel;
    @Mock Path<Object> path;

    private MockedStatic<JpaPropertyUtils> jpaPropertyUtilsMock;

    @BeforeEach
    void setUp() {
      jpaPropertyUtilsMock = mockStatic(JpaPropertyUtils.class);
      jpaPropertyUtilsMock.when(() -> JpaPropertyUtils.getPath(any(), anyString(), any()))
          .thenReturn(path);
    }

    @AfterEach
    void tearDown() {
      jpaPropertyUtilsMock.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAbsFunction_whenToExpression_thenReturnAbsExpression() {
      Expression<Number> numberExpr = mock(Expression.class);
      doReturn(numberExpr).when(cb).abs(any());
      FunctionCall fc = FunctionCall.of(Function.ABS, PropertyReference.of("value"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(numberExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenSqrtFunction_whenToExpression_thenReturnSqrtExpression() {
      Expression<Double> doubleExpr = mock(Expression.class);
      doReturn(doubleExpr).when(cb).sqrt(any());
      FunctionCall fc = FunctionCall.of(Function.SQRT, PropertyReference.of("value"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(doubleExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenModFunction_whenToExpression_thenReturnModExpression() {
      Expression<Integer> intExpr = mock(Expression.class);
      doReturn(intExpr).when(cb).literal(any(Integer.class));
      doReturn(intExpr).when(cb).mod(any(Expression.class), any(Expression.class));
      FunctionCall fc = FunctionCall.of(Function.MOD, PropertyReference.of("value"), Literal.of(10));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(intExpr);
    }
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class StringFunctionTests {
    @Mock Root<?> root;
    @Mock CriteriaBuilder cb;
    @Mock Metamodel metamodel;
    @Mock Path<Object> path;
    @Mock Expression<String> stringExpr;
    @Mock Expression<Integer> intExpr;

    private MockedStatic<JpaPropertyUtils> jpaPropertyUtilsMock;

    @BeforeEach
    void setUp() {
      jpaPropertyUtilsMock = mockStatic(JpaPropertyUtils.class);
      jpaPropertyUtilsMock.when(() -> JpaPropertyUtils.getPath(any(), anyString(), any()))
          .thenReturn(path);
    }

    @AfterEach
    void tearDown() {
      jpaPropertyUtilsMock.close();
    }

    @Test
    void givenConcatFunction_whenToExpression_thenReturnConcatExpression() {
      when(cb.literal(anyString())).thenReturn(stringExpr);
      when(cb.concat(any(Expression.class), any(Expression.class))).thenReturn(stringExpr);
      FunctionCall fc = FunctionCall.of(Function.CONCAT, PropertyReference.of("firstName"), Literal.of(" "), PropertyReference.of("lastName"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isNotNull();
    }

    @Test
    void givenTrimFunction_whenToExpression_thenReturnTrimExpression() {
      when(cb.trim(any(Expression.class))).thenReturn(stringExpr);
      FunctionCall fc = FunctionCall.of(Function.TRIM, PropertyReference.of("name"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(stringExpr);
    }

    @Test
    void givenLtrimFunction_whenToExpression_thenReturnTrimLeadingExpression() {
      when(cb.trim(any(CriteriaBuilder.Trimspec.class), any(Expression.class))).thenReturn(stringExpr);
      FunctionCall fc = FunctionCall.of(Function.LTRIM, PropertyReference.of("name"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(stringExpr);
    }

    @Test
    void givenRtrimFunction_whenToExpression_thenReturnTrimTrailingExpression() {
      when(cb.trim(any(CriteriaBuilder.Trimspec.class), any(Expression.class))).thenReturn(stringExpr);
      FunctionCall fc = FunctionCall.of(Function.RTRIM, PropertyReference.of("name"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(stringExpr);
    }

    @Test
    void givenLowerFunction_whenToExpression_thenReturnLowerExpression() {
      when(cb.lower(any())).thenReturn(stringExpr);
      FunctionCall fc = FunctionCall.of(Function.LOWER, PropertyReference.of("name"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(stringExpr);
    }

    @Test
    void givenUpperFunction_whenToExpression_thenReturnUpperExpression() {
      when(cb.upper(any())).thenReturn(stringExpr);
      FunctionCall fc = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(stringExpr);
    }

    @Test
    void givenLengthFunction_whenToExpression_thenReturnLengthExpression() {
      when(cb.length(any())).thenReturn(intExpr);
      FunctionCall fc = FunctionCall.of(Function.LENGTH, PropertyReference.of("name"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(intExpr);
    }

    @Test
    void givenLocateFunction_whenToExpression_thenReturnLocateExpression() {
      when(cb.literal(anyString())).thenReturn(stringExpr);
      doReturn(intExpr).when(cb).locate(any(Expression.class), any(Expression.class));
      FunctionCall fc = FunctionCall.of(Function.LOCATE, Literal.of("@"), PropertyReference.of("email"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(intExpr);
    }

    @Test
    void givenSubstringFunction_whenToExpression_thenReturnSubstringExpression() {
      when(cb.literal(any(Integer.class))).thenReturn(intExpr);
      when(cb.substring(any(), any(), any())).thenReturn(stringExpr);
      FunctionCall fc = FunctionCall.of(Function.SUBSTRING, PropertyReference.of("name"), Literal.of(1), Literal.of(5));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(stringExpr);
    }

    @Test
    void givenStringFunctionWithNonStringArg_whenToExpression_thenThrowException() {
      FunctionCall fc = FunctionCall.of(Function.LOWER, Literal.of(123));
      assertThatThrownBy(() -> JpaFunctionMapper.toExpression(fc, root, cb, metamodel))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Expected string expression");
    }

    @Test
    void givenIntFunctionWithNonIntArg_whenToExpression_thenThrowException() {
      FunctionCall fc = FunctionCall.of(Function.MOD, PropertyReference.of("x"), Literal.of("notAnInt"));
      assertThatThrownBy(() -> JpaFunctionMapper.toExpression(fc, root, cb, metamodel))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Expected integer expression");
    }
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class DateTimeFunctionTests {
    @Mock Root<?> root;
    @Mock CriteriaBuilder cb;
    @Mock Metamodel metamodel;

    @Test
    @SuppressWarnings("unchecked")
    void givenCurrentDateFunction_whenToExpression_thenReturnCurrentDateExpression() {
      Expression<java.sql.Date> dateExpr = mock(Expression.class);
      doReturn(dateExpr).when(cb).currentDate();
      FunctionCall fc = FunctionCall.of(Function.CURRENT_DATE);
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(dateExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenCurrentTimeFunction_whenToExpression_thenReturnCurrentTimeExpression() {
      Expression<java.sql.Time> timeExpr = mock(Expression.class);
      doReturn(timeExpr).when(cb).currentTime();
      FunctionCall fc = FunctionCall.of(Function.CURRENT_TIME);
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(timeExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenCurrentTimestampFunction_whenToExpression_thenReturnCurrentTimestampExpression() {
      Expression<java.sql.Timestamp> tsExpr = mock(Expression.class);
      doReturn(tsExpr).when(cb).currentTimestamp();
      FunctionCall fc = FunctionCall.of(Function.CURRENT_TIMESTAMP);
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(tsExpr);
    }
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class ConditionalFunctionTests {
    @Mock Root<?> root;
    @Mock CriteriaBuilder cb;
    @Mock Metamodel metamodel;
    @Mock Path<Object> path;

    private MockedStatic<JpaPropertyUtils> jpaPropertyUtilsMock;

    @BeforeEach
    void setUp() {
      jpaPropertyUtilsMock = mockStatic(JpaPropertyUtils.class);
      jpaPropertyUtilsMock.when(() -> JpaPropertyUtils.getPath(any(), anyString(), any()))
          .thenReturn(path);
    }

    @AfterEach
    void tearDown() {
      jpaPropertyUtilsMock.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenCoalesceFunction_whenToExpression_thenReturnCoalesceExpression() {
      CriteriaBuilder.Coalesce<Object> coalesce = mock(CriteriaBuilder.Coalesce.class);
      when(cb.coalesce()).thenReturn(coalesce);
      when(coalesce.value(any())).thenReturn(coalesce);
      FunctionCall fc = FunctionCall.of(Function.COALESCE, PropertyReference.of("nickname"), PropertyReference.of("firstName"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(coalesce);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenNullifFunctionWithLiteral_whenToExpression_thenReturnNullifExpression() {
      Expression<Object> nullifExpr = mock(Expression.class);
      when(cb.nullif(any(Expression.class), any(Object.class))).thenReturn(nullifExpr);
      FunctionCall fc = FunctionCall.of(Function.NULLIF, PropertyReference.of("status"), Literal.of("INACTIVE"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(nullifExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenNullifFunctionWithPropertyExpression_whenToExpression_thenReturnCaseExpression() {
      CriteriaBuilder.Case<Object> caseBuilder = mock(CriteriaBuilder.Case.class);
      Expression<Object> caseExpr = mock(Expression.class);
      Predicate predicate = mock(Predicate.class);
      Expression<Object> nullLit = mock(Expression.class);
      when(cb.selectCase()).thenReturn(caseBuilder);
      when(caseBuilder.when(any(), any())).thenReturn(caseBuilder);
      when(caseBuilder.otherwise(any(Expression.class))).thenReturn(caseExpr);
      when(cb.equal(any(), any())).thenReturn(predicate);
      doReturn(nullLit).when(cb).nullLiteral(any());
      FunctionCall fc = FunctionCall.of(Function.NULLIF, PropertyReference.of("status"), PropertyReference.of("defaultStatus"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(caseExpr);
    }
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class AggregateFunctionTests {
    @Mock Root<?> root;
    @Mock CriteriaBuilder cb;
    @Mock Metamodel metamodel;
    @Mock Path<Object> path;

    private MockedStatic<JpaPropertyUtils> jpaPropertyUtilsMock;

    @BeforeEach
    void setUp() {
      jpaPropertyUtilsMock = mockStatic(JpaPropertyUtils.class);
      jpaPropertyUtilsMock.when(() -> JpaPropertyUtils.getPath(any(), anyString(), any()))
          .thenReturn(path);
    }

    @AfterEach
    void tearDown() {
      jpaPropertyUtilsMock.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenCountFunction_whenToExpression_thenReturnCountExpression() {
      Expression<Long> longExpr = mock(Expression.class);
      when(cb.count(any())).thenReturn(longExpr);
      FunctionCall fc = FunctionCall.of(Function.COUNT, PropertyReference.of("id"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(longExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenSumFunction_whenToExpression_thenReturnSumExpression() {
      Expression<Number> numberExpr = mock(Expression.class);
      doReturn(numberExpr).when(cb).sum(any());
      FunctionCall fc = FunctionCall.of(Function.SUM, PropertyReference.of("amount"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(numberExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAvgFunction_whenToExpression_thenReturnAvgExpression() {
      Expression<Double> doubleExpr = mock(Expression.class);
      doReturn(doubleExpr).when(cb).avg(any());
      FunctionCall fc = FunctionCall.of(Function.AVG, PropertyReference.of("price"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(doubleExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenMinFunction_whenToExpression_thenReturnMinExpression() {
      Expression<Number> numberExpr = mock(Expression.class);
      doReturn(numberExpr).when(cb).min(any());
      FunctionCall fc = FunctionCall.of(Function.MIN, PropertyReference.of("value"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(numberExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenMaxFunction_whenToExpression_thenReturnMaxExpression() {
      Expression<Number> numberExpr = mock(Expression.class);
      doReturn(numberExpr).when(cb).max(any());
      FunctionCall fc = FunctionCall.of(Function.MAX, PropertyReference.of("value"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(numberExpr);
    }
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class LiteralTests {
    @Mock Root<?> root;
    @Mock CriteriaBuilder cb;
    @Mock Metamodel metamodel;
    @Mock Path<Object> path;

    private MockedStatic<JpaPropertyUtils> jpaPropertyUtilsMock;

    @BeforeEach
    void setUp() {
      jpaPropertyUtilsMock = mockStatic(JpaPropertyUtils.class);
      jpaPropertyUtilsMock.when(() -> JpaPropertyUtils.getPath(any(), anyString(), any()))
          .thenReturn(path);
    }

    @AfterEach
    void tearDown() {
      jpaPropertyUtilsMock.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenStringLiteral_whenToExpression_thenReturnLiteralExpression() {
      Expression<String> strExpr = mock(Expression.class);
      when(cb.literal(anyString())).thenReturn(strExpr);
      when(cb.concat(any(Expression.class), any(Expression.class))).thenReturn(strExpr);
      FunctionCall fc = FunctionCall.of(Function.CONCAT, Literal.of("Hello"), Literal.of("World"));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isNotNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenNumberLiteral_whenToExpression_thenReturnLiteralExpression() {
      Expression<Number> numExpr = mock(Expression.class);
      doReturn(numExpr).when(cb).literal(any(Number.class));
      doReturn(numExpr).when(cb).abs(any());
      FunctionCall fc = FunctionCall.of(Function.ABS, Literal.of(-5));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isEqualTo(numExpr);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenBooleanLiteral_whenToExpression_thenReturnLiteralExpression() {
      Expression<Boolean> boolExpr = mock(Expression.class);
      CriteriaBuilder.Coalesce<Object> coalesce = mock(CriteriaBuilder.Coalesce.class);
      when(cb.coalesce()).thenReturn(coalesce);
      when(coalesce.value(any())).thenReturn(coalesce);
      doReturn(boolExpr).when(cb).literal(any(Boolean.class));
      FunctionCall fc = FunctionCall.of(Function.COALESCE, PropertyReference.of("active"), Literal.of(true));
      Expression<?> result = JpaFunctionMapper.toExpression(fc, root, cb, metamodel);
      assertThat(result).isNotNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenNestedFunctions_whenToExpression_thenReturnNestedExpression() {
      Expression<String> strExpr = mock(Expression.class);
      when(cb.trim(any(Expression.class))).thenReturn(strExpr);
      when(cb.upper(any())).thenReturn(strExpr);
      FunctionCall inner = FunctionCall.of(Function.TRIM, PropertyReference.of("name"));
      FunctionCall outer = FunctionCall.of(Function.UPPER, inner);
      Expression<?> result = JpaFunctionMapper.toExpression(outer, root, cb, metamodel);
      assertThat(result).isEqualTo(strExpr);
    }
  }
}
