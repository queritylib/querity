package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.github.queritylib.querity.api.Querity.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimpleGroupByTest {

  @Nested
  class CreationTests {
    @Test
    void givenPropertyNames_whenGroupBy_thenReturnSimpleGroupBy() {
      SimpleGroupBy groupBy = groupBy("gender", "country", "city");

      assertThat(groupBy).isNotNull();
      assertThat(groupBy.getPropertyNames()).containsExactly("gender", "country", "city");
    }

    @Test
    void givenSinglePropertyName_whenGroupBy_thenReturnSimpleGroupBy() {
      SimpleGroupBy groupBy = groupBy("status");

      assertThat(groupBy).isNotNull();
      assertThat(groupBy.getPropertyNames()).containsExactly("status");
    }

    @Test
    void givenPropertyNames_whenOf_thenReturnSimpleGroupBy() {
      SimpleGroupBy groupBy = SimpleGroupBy.of("firstName", "lastName");

      assertThat(groupBy).isNotNull();
      assertThat(groupBy.getPropertyNames()).containsExactly("firstName", "lastName");
    }

    @Test
    void givenPropertyExpressions_whenOfExpressions_thenReturnSimpleGroupBy() {
      SimpleGroupBy groupBy = SimpleGroupBy.ofExpressions(
          upper(prop("category")),
          prop("status")
      );

      assertThat(groupBy).isNotNull();
      assertThat(groupBy.getExpressions()).hasSize(2);
      assertThat(groupBy.getPropertyNames()).isEmpty();
    }

    @Test
    void givenPropertyExpressions_whenGroupBy_thenReturnSimpleGroupBy() {
      SimpleGroupBy groupBy = groupBy(
          lower(prop("name")),
          prop("age")
      );

      assertThat(groupBy).isNotNull();
      assertThat(groupBy.getExpressions()).hasSize(2);
    }
  }

  @Nested
  class EqualsAndHashCodeTests {
    @Test
    void givenTwoGroupBysWithSameProperties_whenEquals_thenReturnTrue() {
      SimpleGroupBy groupBy1 = groupBy("gender", "age");
      SimpleGroupBy groupBy2 = groupBy("gender", "age");

      assertThat(groupBy1)
          .isEqualTo(groupBy2)
          .hasSameHashCodeAs(groupBy2);
    }

    @Test
    void givenTwoGroupBysWithDifferentProperties_whenEquals_thenReturnFalse() {
      SimpleGroupBy groupBy1 = groupBy("gender");
      SimpleGroupBy groupBy2 = groupBy("age");

      assertThat(groupBy1).isNotEqualTo(groupBy2);
    }

    @Test
    void givenTwoGroupBysWithSamePropertiesDifferentOrder_whenEquals_thenReturnFalse() {
      SimpleGroupBy groupBy1 = groupBy("gender", "age");
      SimpleGroupBy groupBy2 = groupBy("age", "gender");

      assertThat(groupBy1).isNotEqualTo(groupBy2);
    }
  }

  @Nested
  class GroupByInterfaceTests {
    @Test
    void givenSimpleGroupBy_whenCheckInterface_thenImplementsGroupBy() {
      SimpleGroupBy groupBy = groupBy("status");

      assertThat(groupBy).isInstanceOf(GroupBy.class);
    }
  }

  @Nested
  class BuilderTests {
    @Test
    void givenSimpleGroupBy_whenToBuilder_thenCanModify() {
      SimpleGroupBy original = groupBy("id", "name");
      SimpleGroupBy modified = original.toBuilder()
          .clearPropertyNames()
          .propertyNames(Arrays.asList("email", "phone"))
          .build();

      assertThat(original.getPropertyNames()).containsExactly("id", "name");
      assertThat(modified.getPropertyNames()).containsExactly("email", "phone");
    }

    @Test
    void givenSimpleGroupBy_whenToBuilderAndAddProperty_thenPropertyIsAdded() {
      SimpleGroupBy original = groupBy("id");
      SimpleGroupBy modified = original.toBuilder()
          .propertyName("name")
          .build();

      assertThat(original.getPropertyNames()).containsExactly("id");
      assertThat(modified.getPropertyNames()).containsExactly("id", "name");
    }
  }

  @Nested
  class UsageInQueryTests {
    @Test
    void givenSimpleGroupBy_whenUsedInAdvancedQuery_thenQueryHasGroupBy() {
      SimpleGroupBy groupBy = groupBy("gender", "country");
      AdvancedQuery query = advancedQuery()
          .groupBy(groupBy)
          .build();

      assertThat(query.hasGroupBy()).isTrue();
      assertThat(query.getGroupBy()).isEqualTo(groupBy);
    }

    @Test
    void givenPropertyNames_whenUsedInAdvancedQueryBuilder_thenQueryHasSimpleGroupBy() {
      AdvancedQuery query = advancedQuery()
          .groupBy("status", "type")
          .build();

      assertThat(query.hasGroupBy()).isTrue();
      assertThat(query.getGroupBy()).isInstanceOf(SimpleGroupBy.class);
    }

    @Test
    void givenPropertyExpressions_whenUsedInAdvancedQueryBuilder_thenQueryHasSimpleGroupBy() {
      AdvancedQuery query = advancedQuery()
          .groupByExpressions(upper(prop("category")))
          .build();

      assertThat(query.hasGroupBy()).isTrue();
      assertThat(query.getGroupBy()).isInstanceOf(SimpleGroupBy.class);
    }

    @Test
    void givenNoGroupBy_whenAdvancedQuery_thenQueryHasNoGroupBy() {
      AdvancedQuery query = advancedQuery().build();

      assertThat(query.hasGroupBy()).isFalse();
      assertThat(query.getGroupBy()).isNull();
    }
  }

  @Nested
  class ToStringTests {
    @Test
    void givenSimpleGroupBy_whenToString_thenContainsPropertyNames() {
      SimpleGroupBy groupBy = groupBy("gender", "country");

      assertThat(groupBy.toString()).contains("gender", "country");
    }
  }

  @Nested
  class ExpressionMethodsTests {
    @Test
    void givenPropertyNames_whenHasExpressions_thenReturnFalse() {
      SimpleGroupBy groupBy = groupBy("name", "age");

      assertThat(groupBy.hasExpressions()).isFalse();
    }

    @Test
    void givenExpressions_whenHasExpressions_thenReturnTrue() {
      SimpleGroupBy groupBy = groupBy(upper(prop("name")));

      assertThat(groupBy.hasExpressions()).isTrue();
    }

    @Test
    void givenPropertyNames_whenGetEffectiveExpressions_thenConvertToPropertyReferences() {
      SimpleGroupBy groupBy = groupBy("firstName", "lastName");

      var expressions = groupBy.getEffectiveExpressions();

      assertThat(expressions).hasSize(2);
      assertThat(expressions.get(0)).isInstanceOf(PropertyReference.class);
    }

    @Test
    void givenExpressions_whenGetEffectiveExpressions_thenReturnSameExpressions() {
      PropertyExpression expr1 = upper(prop("name"));
      PropertyExpression expr2 = prop("age");
      SimpleGroupBy groupBy = groupBy(expr1, expr2);

      var expressions = groupBy.getEffectiveExpressions();

      assertThat(expressions).hasSize(2);
      assertThat(expressions).containsExactly(expr1, expr2);
    }

    @Test
    void givenNullPropertyNames_whenBuild_thenThrowsIllegalArgumentException() {
      assertThatThrownBy(() -> SimpleGroupBy.builder().build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Either propertyNames or expressions must be set");
    }
  }

  @Nested
  class EdgeCaseTests {
    @Test
    void givenNullGroupBy_whenSetInAdvancedQuery_thenQueryHasNoGroupBy() {
      AdvancedQuery query = advancedQuery().groupBy((GroupBy) null).build();

      assertThat(query.hasGroupBy()).isFalse();
      assertThat(query.getGroupBy()).isNull();
    }

    @Test
    void givenEmptyPropertyNamesArray_whenGroupBy_thenThrowsIllegalArgumentException() {
      assertThatThrownBy(() -> SimpleGroupBy.of())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Either propertyNames or expressions must be set");
    }

    @Test
    void givenEmptyExpressionsArray_whenOfExpressions_thenThrowsIllegalArgumentException() {
      assertThatThrownBy(() -> SimpleGroupBy.ofExpressions())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Either propertyNames or expressions must be set");
    }

    @Test
    void givenBothPropertyNamesAndExpressions_whenBuild_thenCombinesBoth() {
      SimpleGroupBy groupBy = SimpleGroupBy.builder()
          .propertyName("category")
          .expression(upper(prop("name")))
          .build();
      
      assertThat(groupBy.hasPropertyNames()).isTrue();
      assertThat(groupBy.hasExpressions()).isTrue();
      assertThat(groupBy.getPropertyNames()).containsExactly("category");
      assertThat(groupBy.getExpressions()).hasSize(1);
      
      // Effective expressions should contain both: propertyNames first, then expressions
      List<PropertyExpression> effective = groupBy.getEffectiveExpressions();
      assertThat(effective).hasSize(2);
      assertThat(effective.get(0)).isInstanceOf(PropertyReference.class);
      assertThat(effective.get(1)).isInstanceOf(FunctionCall.class);
    }

    @Test
    void givenPropertyNamesListModified_whenGetPropertyNames_thenOriginalUnchanged() {
      SimpleGroupBy groupBy = groupBy("category", "status");
      
      // Attempt to modify returned list should throw UnsupportedOperationException
      var names = groupBy.getPropertyNames();
      assertThatThrownBy(() -> names.add("newProperty"))
          .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void givenExpressionsListModified_whenGetExpressions_thenOriginalUnchanged() {
      SimpleGroupBy groupBy = groupBy(upper(prop("category")));
      
      // Attempt to modify returned list should throw UnsupportedOperationException
      var exprs = groupBy.getExpressions();
      assertThatThrownBy(() -> exprs.add(prop("newProp")))
          .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void givenEffectiveExpressionsListModified_whenGetEffectiveExpressions_thenOriginalUnchanged() {
      SimpleGroupBy groupBy = groupBy(upper(prop("category")));
      
      // Attempt to modify returned list should throw UnsupportedOperationException
      var exprs = groupBy.getEffectiveExpressions();
      assertThatThrownBy(() -> exprs.add(prop("newProp")))
          .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void givenPropertyNames_whenGetEffectiveExpressions_thenListIsImmutable() {
      SimpleGroupBy groupBy = groupBy("category", "status");
      
      // Attempt to modify returned list should throw UnsupportedOperationException
      var exprs = groupBy.getEffectiveExpressions();
      assertThatThrownBy(() -> exprs.add(prop("newProp")))
          .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void givenHavingWithoutGroupBy_whenBuild_thenThrowsIllegalStateException() {
      assertThatThrownBy(() -> 
          advancedQuery()
              .having(filterBy(count(prop("id")), Operator.GREATER_THAN, 5))
              .build())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("HAVING clause requires a GROUP BY clause");
    }

    @Test
    void givenHavingWithGroupBy_whenBuild_thenSucceeds() {
      AdvancedQuery q = advancedQuery()
          .groupBy("category")
          .having(filterBy(count(prop("id")), Operator.GREATER_THAN, 5))
          .build();

      assertThat(q.hasGroupBy()).isTrue();
      assertThat(q.hasHaving()).isTrue();
    }
  }
}
