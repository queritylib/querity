package io.github.queritylib.querity.api;

import io.github.queritylib.querity.api.domain.Person;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.github.queritylib.querity.api.Operator.*;
import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.SimpleSort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;

class QuerityApiTests {

  private final Querity querity = new QuerityDummyImpl();

  @Nested
  class FilteringTests {
    @Test
    void givenEmptyQuery_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneEqualsCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", EQUALS, "Skywalker"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneNotEqualsCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", NOT_EQUALS, "Skywalker"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneStartsWithCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", STARTS_WITH, "Sky"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneEndsWithCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", ENDS_WITH, "walker"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneContainsCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", CONTAINS, "walk"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneGreaterThanCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("height", GREATER_THAN, "1.72"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneGreaterThanEqualsCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("height", GREATER_THAN_EQUALS, "1.72"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneLesserThanCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("height", LESSER_THAN, "1.72"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneLesserThanEqualsCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("height", LESSER_THAN_EQUALS, "1.72"))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneIsNullCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", IS_NULL))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneIsNotNullCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", IS_NOT_NULL))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneInListCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", IN, List.of("Skywalker", "Solo")))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithOneNotInListCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterBy("lastName", NOT_IN, List.of("Skywalker", "Solo")))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithNotConditionContainingSimpleCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(not(filterBy("lastName", EQUALS, "Skywalker")))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithNotConditionContainingConditionsWrapper_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(
                  not(or(
                      filterBy("firstName", EQUALS, "Luke"),
                      filterBy("lastName", EQUALS, "Skywalker")
                  ))
              ).build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithTwoEqualsConditions_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(and(
                  filterBy("firstName", EQUALS, "Luke"),
                  filterBy("lastName", EQUALS, "Skywalker")
              ))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithTwoEqualsConditionsWithOrLogic_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(or(
                  filterBy("firstName", EQUALS, "Luke"),
                  filterBy("lastName", EQUALS, "Skywalker")
              ))
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithNestedConditions_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(and(
                  filterBy("lastName", EQUALS, "Skywalker"),
                  or(
                      filterBy("firstName", EQUALS, "Anakin"),
                      filterBy("firstName", EQUALS, "Luke")
                  ))
              )
              .build());
      assertThat(people).isNotNull();
    }

    @Test
    void givenFilterWithNativeCondition_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .filter(filterByNative(new Object()))
              .build());
      assertThat(people).isNotNull();
    }
  }

  @Nested
  class PaginationTests {
    @Test
    void givenPagination_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .pagination(1, 5)
              .build());
      assertThat(people).isNotNull();
    }
  }

  @Nested
  class SortingTests {
    @Test
    void givenSort_whenFindAll_thenReturnListOfEntity() {
      List<Person> people = querity.findAll(Person.class,
          Querity.query()
              .sort(sortBy("lastName"), sortBy("firstName", DESC))
              .build());
      assertThat(people).isNotNull();
    }
  }

  @Nested
  class CountTests {
    @Test
    void givenSimpleCondition_whenCount_thenReturnALong() {
      Long count = querity.count(Person.class,
          filterBy("lastName", EQUALS, "Skywalker"));
      assertThat(count).isNotNull();
    }
  }

  @Nested
  class PreprocessingTests {
    private final QueryPreprocessor dummyPreprocessor = query -> query;

    @Test
    void givenQuery_whenWithPreprocessor_thenAddPreprocessorToTheList() {
      Query query = Querity.query().withPreprocessor(dummyPreprocessor).build();
      assertThat(query.getPreprocessors()).contains(dummyPreprocessor);
    }
  }

  @Test
  void givenCondition_whenWrapConditionInQuery_thenReturnQueryWithJustTheCondition() {
    Condition condition = SimpleCondition.builder()
        .propertyName("prop1")
        .operator(IS_NULL)
        .build();
    Query query = Querity.wrapConditionInQuery(condition);
    assertThat(query.getFilter()).isEqualTo(condition);
    assertThat(query.hasSort()).isFalse();
    assertThat(query.hasPagination()).isFalse();
  }

  static class QuerityDummyImpl implements Querity {
    @Override
    public <T> List<T> findAll(Class<T> entityClass, Query query) {
      return Collections.emptyList();
    }

    @Override
    public <T> Long count(Class<T> entityClass, Condition condition) {
      return 0L;
    }
  }
}
