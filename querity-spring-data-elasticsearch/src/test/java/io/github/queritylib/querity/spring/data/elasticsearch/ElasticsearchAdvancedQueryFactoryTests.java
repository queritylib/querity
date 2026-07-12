package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.AdvancedQuery;
import io.github.queritylib.querity.spring.data.elasticsearch.domain.Person;
import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Querity.groupBy;
import static io.github.queritylib.querity.api.Querity.selectBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElasticsearchAdvancedQueryFactoryTests {

  @Test
  void givenNullQuery_whenGetElasticsearchProjectedQuery_thenThrowIllegalStateException() {
    ElasticsearchAdvancedQueryFactory<Person> factory = new ElasticsearchAdvancedQueryFactory<>(Person.class, null);

    assertThatThrownBy(factory::getElasticsearchProjectedQuery)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("SELECT clause");
  }

  @Test
  void givenQueryWithoutSelect_whenGetElasticsearchProjectedQuery_thenThrowIllegalStateException() {
    AdvancedQuery query = AdvancedQuery.builder().build();
    ElasticsearchAdvancedQueryFactory<Person> factory = new ElasticsearchAdvancedQueryFactory<>(Person.class, query);

    assertThatThrownBy(factory::getElasticsearchProjectedQuery)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("SELECT clause");
  }

  @Test
  void givenDistinctQueryWithSelect_whenGetElasticsearchProjectedQuery_thenIgnoreDistinctFlag() {
    AdvancedQuery query = AdvancedQuery.builder()
        .select(selectBy("firstName"))
        .distinct(true)
        .build();
    ElasticsearchAdvancedQueryFactory<Person> factory = new ElasticsearchAdvancedQueryFactory<>(Person.class, query);

    org.springframework.data.elasticsearch.core.query.Query result = factory.getElasticsearchProjectedQuery();

    assertThat(result.getSourceFilter()).isNotNull();
    assertThat(result.getSourceFilter().getIncludes()).contains("firstName");
  }

  @Test
  void givenQueryWithGroupBy_whenGetElasticsearchProjectedQuery_thenThrowUnsupportedOperationException() {
    AdvancedQuery query = AdvancedQuery.builder()
        .select(selectBy("firstName"))
        .groupBy(groupBy("lastName"))
        .build();
    ElasticsearchAdvancedQueryFactory<Person> factory = new ElasticsearchAdvancedQueryFactory<>(Person.class, query);

    assertThatThrownBy(factory::getElasticsearchProjectedQuery)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("GROUP BY");
  }
}
