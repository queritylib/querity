package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.AdvancedQuery;
import io.github.queritylib.querity.spring.data.mongodb.domain.Person;
import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Querity.groupBy;
import static io.github.queritylib.querity.api.Querity.selectBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MongodbAdvancedQueryFactoryTests {

  @Test
  void givenNullQuery_whenGetMongodbProjectedQuery_thenThrowIllegalStateException() {
    MongodbAdvancedQueryFactory<Person> factory = new MongodbAdvancedQueryFactory<>(Person.class, null);

    assertThatThrownBy(factory::getMongodbProjectedQuery)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("SELECT clause");
  }

  @Test
  void givenQueryWithoutSelect_whenGetMongodbProjectedQuery_thenThrowIllegalStateException() {
    AdvancedQuery query = AdvancedQuery.builder().build();
    MongodbAdvancedQueryFactory<Person> factory = new MongodbAdvancedQueryFactory<>(Person.class, query);

    assertThatThrownBy(factory::getMongodbProjectedQuery)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("SELECT clause");
  }

  @Test
  void givenDistinctQueryWithSelect_whenGetMongodbProjectedQuery_thenIgnoreDistinctFlag() {
    AdvancedQuery query = AdvancedQuery.builder()
        .select(selectBy("firstName"))
        .distinct(true)
        .build();
    MongodbAdvancedQueryFactory<Person> factory = new MongodbAdvancedQueryFactory<>(Person.class, query);

    org.springframework.data.mongodb.core.query.Query result = factory.getMongodbProjectedQuery();

    assertThat(result.getFieldsObject()).containsKey("firstName");
  }

  @Test
  void givenQueryWithGroupBy_whenGetMongodbProjectedQuery_thenThrowUnsupportedOperationException() {
    AdvancedQuery query = AdvancedQuery.builder()
        .select(selectBy("firstName"))
        .groupBy(groupBy("lastName"))
        .build();
    MongodbAdvancedQueryFactory<Person> factory = new MongodbAdvancedQueryFactory<>(Person.class, query);

    assertThatThrownBy(factory::getMongodbProjectedQuery)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("GROUP BY");
  }
}
