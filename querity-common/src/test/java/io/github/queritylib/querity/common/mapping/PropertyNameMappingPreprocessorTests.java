package io.github.queritylib.querity.common.mapping;

import io.github.queritylib.querity.api.*;
import org.junit.jupiter.api.Test;

import static io.github.queritylib.querity.api.Operator.EQUALS;
import static io.github.queritylib.querity.api.Querity.*;
import static io.github.queritylib.querity.api.Sort.Direction.DESC;
import static org.assertj.core.api.Assertions.assertThat;

class PropertyNameMappingPreprocessorTests {

  public static final PropertyNameMappingPreprocessor PREPROCESSOR = new PropertyNameMappingPreprocessor(SimplePropertyNameMapper.builder()
      .mapping("prop1", "prop2")
      .build());

  @Test
  void givenQueryWithSimpleCondition_whenFindAll_thenCallDoFindAllWithPreprocessedQuery() {
    Query q = Querity.query().withPreprocessor(PREPROCESSOR)
        .filter(filterBy("prop1", EQUALS, "test"))
        .build()
        .preprocess();

    assertThat(q.getFilter()).isInstanceOf(SimpleCondition.class);
    assertThat(((SimpleCondition) q.getFilter()).getPropertyName()).isEqualTo("prop2");
  }

  @Test
  void givenQueryWithAndConditionsWrapper_whenFindAll_thenCallDoFindAllWithPreprocessedQuery() {
    Query q = Querity.query().withPreprocessor(PREPROCESSOR)
        .filter(and(
            filterBy("prop1", EQUALS, "test"),
            filterBy("prop3", EQUALS, "test")
        ))
        .build()
        .preprocess();

    assertThat(q.getFilter()).isInstanceOf(AndConditionsWrapper.class);
    assertThat(((AndConditionsWrapper) q.getFilter()).getConditions()).hasSize(2);
    assertThat(((AndConditionsWrapper) q.getFilter()).getConditions().get(0)).isInstanceOf(SimpleCondition.class);
    assertThat(((SimpleCondition) ((AndConditionsWrapper) q.getFilter()).getConditions().get(0)).getPropertyName()).isEqualTo("prop2");
    assertThat(((AndConditionsWrapper) q.getFilter()).getConditions().get(1)).isInstanceOf(SimpleCondition.class);
    assertThat(((SimpleCondition) ((AndConditionsWrapper) q.getFilter()).getConditions().get(1)).getPropertyName()).isEqualTo("prop3");
  }

  @Test
  void givenQueryWithOrConditionsWrapper_whenFindAll_thenCallDoFindAllWithPreprocessedQuery() {
    Query q = Querity.query().withPreprocessor(PREPROCESSOR)
        .filter(or(
            filterBy("prop1", EQUALS, "test"),
            filterBy("prop3", EQUALS, "test")
        ))
        .build()
        .preprocess();

    assertThat(q.getFilter()).isInstanceOf(OrConditionsWrapper.class);
    assertThat(((OrConditionsWrapper) q.getFilter()).getConditions()).hasSize(2);
    assertThat(((OrConditionsWrapper) q.getFilter()).getConditions().get(0)).isInstanceOf(SimpleCondition.class);
    assertThat(((SimpleCondition) ((OrConditionsWrapper) q.getFilter()).getConditions().get(0)).getPropertyName()).isEqualTo("prop2");
    assertThat(((OrConditionsWrapper) q.getFilter()).getConditions().get(1)).isInstanceOf(SimpleCondition.class);
    assertThat(((SimpleCondition) ((OrConditionsWrapper) q.getFilter()).getConditions().get(1)).getPropertyName()).isEqualTo("prop3");
  }

  @Test
  void givenQueryWithNotCondition_whenFindAll_thenCallDoFindAllWithPreprocessedQuery() {
    Query q = Querity.query().withPreprocessor(PREPROCESSOR)
        .filter(not(
            filterBy("prop1", EQUALS, "test")
        ))
        .build()
        .preprocess();

    assertThat(q.getFilter()).isInstanceOf(NotCondition.class);
    assertThat(q.getFilter().isEmpty()).isFalse();
    assertThat(((NotCondition) q.getFilter()).getCondition()).isInstanceOf(SimpleCondition.class);
    assertThat(((SimpleCondition) ((NotCondition) q.getFilter()).getCondition()).getPropertyName()).isEqualTo("prop2");
  }

  @Test
  void givenQueryWithSort_whenFindAll_thenCallDoFindAllWithPreprocessedQuery() {
    Query q = Querity.query().withPreprocessor(PREPROCESSOR)
        .sort(sortBy("prop1"), sortBy("prop3", DESC))
        .build()
        .preprocess();

    assertThat(q.hasSort()).isTrue();
    assertThat(q.getSort().get(0).getPropertyName()).isEqualTo("prop2");
    assertThat(q.getSort().get(1).getPropertyName()).isEqualTo("prop3");
  }
}
