package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.Operator.EQUALS;
import static io.github.queritylib.querity.api.SimpleSort.Direction.ASC;
import static io.github.queritylib.querity.api.SimpleSort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdvancedQueryTests {

  @Nested
  class BuilderTests {

    @Test
    void givenNoParameters_whenBuild_thenReturnEmptyQuery() {
      AdvancedQuery query = advancedQuery().build();
      assertThat(query.hasFilter()).isFalse();
      assertThat(query.hasPagination()).isFalse();
      assertThat(query.hasSort()).isFalse();
      assertThat(query.hasSelect()).isFalse();
      assertThat(query.hasGroupBy()).isFalse();
      assertThat(query.hasHaving()).isFalse();
      assertThat(query.isDistinct()).isFalse();
    }

    @Test
    void givenFilter_whenBuild_thenQueryHasFilter() {
      Condition filter = filterBy("status", EQUALS, "ACTIVE");
      AdvancedQuery query = advancedQuery().filter(filter).build();
      assertThat(query.hasFilter()).isTrue();
      assertThat(query.getFilter()).isEqualTo(filter);
    }

    @Test
    void givenSort_whenBuild_thenQueryHasSort() {
      Sort sort = sortBy("lastName", ASC);
      AdvancedQuery query = advancedQuery().sort(sort).build();
      assertThat(query.hasSort()).isTrue();
      assertThat(query.getSort()).hasSize(1);
      assertThat(query.getSort()).contains(sort);
    }

    @Test
    void givenMultipleSorts_whenBuild_thenQueryHasAllSorts() {
      Sort sort1 = sortBy("lastName", ASC);
      Sort sort2 = sortBy("firstName", DESC);
      AdvancedQuery query = advancedQuery().sort(sort1, sort2).build();
      assertThat(query.getSort()).hasSize(2);
      assertThat(query.getSort()).containsExactly(sort1, sort2);
    }

    @Test
    void givenPagination_whenBuild_thenQueryHasPagination() {
      AdvancedQuery query = advancedQuery().pagination(1, 20).build();
      assertThat(query.hasPagination()).isTrue();
      assertThat(query.getPagination().getPage()).isEqualTo(1);
      assertThat(query.getPagination().getPageSize()).isEqualTo(20);
    }

    @Test
    void givenPaginationObject_whenBuild_thenQueryHasPagination() {
      Pagination pagination = paged(2, 15);
      AdvancedQuery query = advancedQuery().pagination(pagination).build();
      assertThat(query.hasPagination()).isTrue();
      assertThat(query.getPagination()).isEqualTo(pagination);
    }

    @Test
    void givenDistinct_whenBuild_thenQueryIsDistinct() {
      AdvancedQuery query = advancedQuery().distinct(true).build();
      assertThat(query.isDistinct()).isTrue();
    }

    @Test
    void givenSelectByPropertyNames_whenBuild_thenQueryHasSelect() {
      AdvancedQuery query = advancedQuery().selectBy("firstName", "lastName").build();
      assertThat(query.hasSelect()).isTrue();
    }

    @Test
    void givenSelectByExpressions_whenBuild_thenQueryHasSelect() {
      AdvancedQuery query = advancedQuery()
          .select(prop("category"), sum(prop("amount")).as("total"))
          .build();
      assertThat(query.hasSelect()).isTrue();
    }

    @Test
    void givenGroupByPropertyNames_whenBuild_thenQueryHasGroupBy() {
      AdvancedQuery query = advancedQuery()
          .selectBy("category")
          .groupBy("category")
          .build();
      assertThat(query.hasGroupBy()).isTrue();
    }

    @Test
    void givenGroupByExpressions_whenBuild_thenQueryHasGroupBy() {
      AdvancedQuery query = advancedQuery()
          .selectBy("category")
          .groupByExpressions(prop("category"))
          .build();
      assertThat(query.hasGroupBy()).isTrue();
    }

    @Test
    void givenHavingWithGroupBy_whenBuild_thenQueryHasHaving() {
      Condition having = filterBy(count(prop("id")), Operator.GREATER_THAN, 5);
      AdvancedQuery query = advancedQuery()
          .selectBy("category")
          .groupBy("category")
          .having(having)
          .build();
      assertThat(query.hasHaving()).isTrue();
      assertThat(query.getHaving()).isEqualTo(having);
    }

    @Test
    void givenHavingWithoutGroupBy_whenBuild_thenThrowException() {
      Condition having = filterBy(count(prop("id")), Operator.GREATER_THAN, 5);
      assertThatThrownBy(() -> advancedQuery().having(having).build())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("HAVING clause requires a GROUP BY clause");
    }

    @Test
    void givenNullCustomizer_whenCustomize_thenThrowException() {
      assertThatThrownBy(() -> advancedQuery().customize((QueryCustomizer<?>[]) null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Customizers cannot be null");
    }

    @Test
    void givenCustomizer_whenCustomize_thenQueryHasCustomizer() {
      QueryCustomizer<?> customizer = q -> {};
      AdvancedQuery query = advancedQuery().customize(customizer).build();
      assertThat(query.getCustomizers()).containsExactly(customizer);
    }

    @Test
    void givenMultipleCustomizers_whenCustomize_thenQueryHasAllCustomizers() {
      QueryCustomizer<?> customizer1 = q -> {};
      QueryCustomizer<?> customizer2 = q -> {};
      AdvancedQuery query = advancedQuery().customize(customizer1, customizer2).build();
      assertThat(query.getCustomizers()).containsExactly(customizer1, customizer2);
    }
  }

  @Nested
  class PreprocessorTests {

    @Test
    void givenPreprocessor_whenPreprocess_thenCallPreprocessor() {
      AdvancedQueryPreprocessor preprocessor = Mockito.spy(new DummyAdvancedQueryPreprocessor());
      AdvancedQuery query = advancedQuery().withPreprocessor(preprocessor).build();
      query.preprocess();

      Mockito.verify(preprocessor).preprocess(query);
    }

    @Test
    void givenMultiplePreprocessors_whenPreprocess_thenCallAllInOrder() {
      AdvancedQueryPreprocessor preprocessor1 = Mockito.spy(new DummyAdvancedQueryPreprocessor());
      AdvancedQueryPreprocessor preprocessor2 = Mockito.spy(new DummyAdvancedQueryPreprocessor());

      AdvancedQuery query = advancedQuery()
          .withPreprocessor(preprocessor1)
          .withPreprocessor(preprocessor2)
          .build();
      query.preprocess();

      Mockito.verify(preprocessor1).preprocess(query);
      Mockito.verify(preprocessor2).preprocess(Mockito.any(AdvancedQuery.class));
    }

    @Test
    void givenTransformingPreprocessor_whenPreprocess_thenReturnTransformedQuery() {
      AdvancedQueryPreprocessor addFilterPreprocessor = q -> q.toBuilder()
          .filter(filterBy("tenantId", EQUALS, "TENANT-1"))
          .build();

      AdvancedQuery query = advancedQuery()
          .selectBy("id", "name")
          .withPreprocessor(addFilterPreprocessor)
          .build();

      AdvancedQuery processed = query.preprocess();

      assertThat(processed.hasFilter()).isTrue();
      assertThat(processed.hasSelect()).isTrue();
    }

    @Test
    void givenNoPreprocessors_whenPreprocess_thenReturnSameQuery() {
      AdvancedQuery query = advancedQuery().selectBy("id", "name").build();
      AdvancedQuery processed = query.preprocess();
      assertThat(processed).isSameAs(query);
    }

    @Test
    void givenNullPreprocessor_whenWithPreprocessor_thenThrowException() {
      assertThatThrownBy(() -> advancedQuery().withPreprocessor(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Preprocessor cannot be null");
    }
  }

  @Nested
  class ToBuilderTests {

    @Test
    void givenQuery_whenToBuilder_thenCanModify() {
      AdvancedQuery original = advancedQuery()
          .selectBy("firstName")
          .filter(filterBy("status", EQUALS, "ACTIVE"))
          .build();

      AdvancedQuery modified = original.toBuilder()
          .distinct(true)
          .build();

      assertThat(modified.isDistinct()).isTrue();
      assertThat(modified.hasFilter()).isTrue();
      assertThat(modified.hasSelect()).isTrue();
    }
  }

  static class DummyAdvancedQueryPreprocessor implements AdvancedQueryPreprocessor {
    @Override
    public AdvancedQuery preprocess(AdvancedQuery query) {
      return query;
    }
  }
}
