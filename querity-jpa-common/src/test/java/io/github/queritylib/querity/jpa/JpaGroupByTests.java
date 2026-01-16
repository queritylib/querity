package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.GroupBy;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.api.SimpleGroupBy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
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

import java.util.List;

import static io.github.queritylib.querity.api.Querity.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class JpaGroupByTests {

  @Nested
  class FactoryMethodTests {
    @Test
    void givenSimpleGroupBy_whenOf_thenReturnJpaSimpleGroupBy() {
      SimpleGroupBy simpleGroupBy = groupBy("gender", "country");

      JpaGroupBy jpaGroupBy = JpaGroupBy.of(simpleGroupBy);

      assertThat(jpaGroupBy).isInstanceOf(JpaSimpleGroupBy.class);
    }

    @Test
    void givenUnsupportedGroupBy_whenOf_thenThrowException() {
      GroupBy unsupportedGroupBy = new UnsupportedGroupBy();

      assertThatThrownBy(() -> JpaGroupBy.of(unsupportedGroupBy))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unsupported GroupBy type");
    }
  }

  @Nested
  @ExtendWith(MockitoExtension.class)
  class ToExpressionsTests {
    @Mock Root<?> root;
    @Mock CriteriaBuilder cb;
    @Mock Metamodel metamodel;
    @Mock Path<Object> path;
    @Mock Path<Object> path2;
    @Mock Expression<String> upperExpr;

    private MockedStatic<JpaPropertyUtils> jpaPropertyUtilsMock;
    private MockedStatic<JpaFunctionMapper> jpaFunctionMapperMock;

    @BeforeEach
    void setUp() {
      jpaPropertyUtilsMock = mockStatic(JpaPropertyUtils.class);
      jpaPropertyUtilsMock.when(() -> JpaPropertyUtils.getPath(any(), anyString(), any()))
          .thenReturn(path);
    }

    @AfterEach
    void tearDown() {
      jpaPropertyUtilsMock.close();
      if (jpaFunctionMapperMock != null) {
        jpaFunctionMapperMock.close();
      }
    }

    @Test
    void givenSimpleGroupByWithSinglePropertyName_whenToExpressions_thenReturnSinglePath() {
      SimpleGroupBy simpleGroupBy = groupBy("category");
      JpaSimpleGroupBy jpaSimpleGroupBy = new JpaSimpleGroupBy(simpleGroupBy);

      List<Expression<?>> expressions = jpaSimpleGroupBy.toExpressions(metamodel, root, cb);

      assertThat(expressions).hasSize(1);
      assertThat(expressions.get(0)).isEqualTo(path);
    }

    @Test
    void givenSimpleGroupByWithMultiplePropertyNames_whenToExpressions_thenReturnMultiplePaths() {
      SimpleGroupBy simpleGroupBy = groupBy("category", "status", "region");
      JpaSimpleGroupBy jpaSimpleGroupBy = new JpaSimpleGroupBy(simpleGroupBy);

      List<Expression<?>> expressions = jpaSimpleGroupBy.toExpressions(metamodel, root, cb);

      assertThat(expressions).hasSize(3);
    }

    @Test
    void givenSimpleGroupByWithExpressions_whenToExpressions_thenReturnExpressionResults() {
      jpaFunctionMapperMock = mockStatic(JpaFunctionMapper.class);
      jpaFunctionMapperMock.when(() -> JpaFunctionMapper.toExpression(any(), any(), any(), any()))
          .thenReturn(upperExpr);

      SimpleGroupBy simpleGroupBy = groupBy(upper(prop("category")));
      JpaSimpleGroupBy jpaSimpleGroupBy = new JpaSimpleGroupBy(simpleGroupBy);

      List<Expression<?>> expressions = jpaSimpleGroupBy.toExpressions(metamodel, root, cb);

      assertThat(expressions).hasSize(1);
      assertThat(expressions.get(0)).isEqualTo(upperExpr);
    }

    @Test
    void givenSimpleGroupByWithMultipleExpressions_whenToExpressions_thenReturnAllExpressions() {
      jpaFunctionMapperMock = mockStatic(JpaFunctionMapper.class);
      jpaFunctionMapperMock.when(() -> JpaFunctionMapper.toExpression(any(), any(), any(), any()))
          .thenReturn(upperExpr);

      SimpleGroupBy simpleGroupBy = groupBy(upper(prop("category")), lower(prop("name")));
      JpaSimpleGroupBy jpaSimpleGroupBy = new JpaSimpleGroupBy(simpleGroupBy);

      List<Expression<?>> expressions = jpaSimpleGroupBy.toExpressions(metamodel, root, cb);

      assertThat(expressions).hasSize(2);
    }

    @Test
    void givenSimpleGroupByWithPropertyNames_whenToExpressions_thenUsesPropertyNamesPath() {
      // Verify that when using property names (not expressions), hasExpressions returns false
      SimpleGroupBy simpleGroupBy = groupBy("field1", "field2");
      assertThat(simpleGroupBy.hasExpressions()).isFalse();
      
      JpaSimpleGroupBy jpaSimpleGroupBy = new JpaSimpleGroupBy(simpleGroupBy);
      List<Expression<?>> expressions = jpaSimpleGroupBy.toExpressions(metamodel, root, cb);

      assertThat(expressions).hasSize(2);
    }

    @Test
    void givenSimpleGroupByWithPropertyExpressions_whenToExpressions_thenUsesExpressionsPath() {
      jpaFunctionMapperMock = mockStatic(JpaFunctionMapper.class);
      jpaFunctionMapperMock.when(() -> JpaFunctionMapper.toExpression(any(), any(), any(), any()))
          .thenReturn(upperExpr);

      // Verify that when using expressions, hasExpressions returns true
      SimpleGroupBy simpleGroupBy = groupBy(prop("category"));
      assertThat(simpleGroupBy.hasExpressions()).isTrue();
      
      JpaSimpleGroupBy jpaSimpleGroupBy = new JpaSimpleGroupBy(simpleGroupBy);
      List<Expression<?>> expressions = jpaSimpleGroupBy.toExpressions(metamodel, root, cb);

      assertThat(expressions).hasSize(1);
    }
  }

  // Helper class for testing unsupported GroupBy
  private static class UnsupportedGroupBy implements GroupBy {
  }
  
  // Note: FindAllWithGroupByTests was removed because Query no longer supports GROUP BY.
  // GROUP BY is now only available on AdvancedQuery, which must be used with findAllProjected().
  // The type system now enforces this at compile time, making runtime validation unnecessary.
}
