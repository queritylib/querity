package io.github.queritylib.querity.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.github.queritylib.querity.api.Operator.EQUALS;
import static io.github.queritylib.querity.api.Querity.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryTests {
  @Test
  void givenNoFilter_whenHasFilter_thenReturnsFalse() {
    Query query = Querity.query().build();
    assertThat(query.hasFilter()).isFalse();
  }

  @Test
  void givenFilterWithEmptyCondition_whenHasFilter_thenReturnsFalse() {
    Query query = Querity.query().filter(and()).build();
    assertThat(query.hasFilter()).isFalse();
  }

  @Test
  void givenFilter_whenHasFilter_thenReturnsTrue() {
    Query query = Querity.query().filter(filterBy("lastName", EQUALS, "Skywalker")).build();
    assertThat(query.hasFilter()).isTrue();
  }

  @Test
  void givenNoPagination_whenHasPagination_thenReturnsFalse() {
    Query query = Querity.query().build();
    assertThat(query.hasPagination()).isFalse();
  }

  @Test
  void givenPagination_whenHasPagination_thenReturnsTrue() {
    Query query = Querity.query().pagination(1, 20).build();
    assertThat(query.hasPagination()).isTrue();
  }

  @Test
  void givenPagination2_whenHasPagination_thenReturnsTrue() {
    Query query = Querity.query().pagination(paged(1, 20)).build();
    assertThat(query.hasPagination()).isTrue();
  }

  @Test
  void givenNoSort_whenHasSort_thenReturnsFalse() {
    Query query = Querity.query().build();
    assertThat(query.hasSort()).isFalse();
  }

  @Test
  void givenSort_whenHasSort_thenReturnsTrue() {
    Query query = Querity.query().sort(sortBy("lastName")).build();
    assertThat(query.hasSort()).isTrue();
  }

  @Test
  void givenSort_whenGetSort_thenReturnsListContainingTheSort() {
    Sort sort = sortBy("lastName");
    Query query = Querity.query().sort(sort).build();
    assertThat(query.getSort()).hasSize(1);
    assertThat(query.getSort()).contains(sort);
  }

  @Test
  void givenPreprocessor_whenPreprocess_thenCallPreprocessors() {
    QueryPreprocessor preprocessor = Mockito.spy(new DummyQueryPreprocessor());
    Query query = Querity.query().withPreprocessor(preprocessor).build();
    query.preprocess();

    Mockito.verify(preprocessor).preprocess(query);
  }

  @Test
  void givenNullPreprocessor_whenWithPreprocessor_thenThrowException() {
    assertThatThrownBy(() -> Querity.query().withPreprocessor(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Preprocessor cannot be null");
  }

  @Test
  void givenNullCustomizer_whenCustomize_thenThrowException() {
    assertThatThrownBy(() -> Querity.query().customize((QueryCustomizer<?>[]) null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Customizers cannot be null");
  }

  @Test
  void givenCustomizer_whenCustomize_thenAddCustomizer() {
    QueryCustomizer<?> customizer = q -> {};
    Query query = Querity.query().customize(customizer).build();
    assertThat(query.getCustomizers()).containsExactly(customizer);
  }

  @Test
  void givenMultipleCustomizers_whenCustomize_thenAddAllCustomizers() {
    QueryCustomizer<?> customizer1 = q -> {};
    QueryCustomizer<?> customizer2 = q -> {};
    Query query = Querity.query()
        .customize(customizer1)
        .customize(customizer2)
        .build();
    assertThat(query.getCustomizers()).containsExactly(customizer1, customizer2);
  }

  @Test
  void givenCustomizersArray_whenCustomize_thenAddAllCustomizers() {
    QueryCustomizer<?> customizer1 = q -> {};
    QueryCustomizer<?> customizer2 = q -> {};
    Query query = Querity.query()
        .customize(customizer1, customizer2)
        .build();
    assertThat(query.getCustomizers()).containsExactly(customizer1, customizer2);
  }

  static class DummyQueryPreprocessor implements QueryPreprocessor {
    @Override
    public Query preprocess(Query query) {
      return query;
    }
  }
}
