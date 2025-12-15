package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Select;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.queritylib.querity.api.Querity.selectBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElasticsearchSelectTests {

  @Test
  void givenSimpleSelect_whenOf_thenReturnElasticsearchSimpleSelect() {
    Select simpleSelect = selectBy("id", "name");

    ElasticsearchSelect elasticsearchSelect = ElasticsearchSelect.of(simpleSelect);

    assertThat(elasticsearchSelect).isInstanceOf(ElasticsearchSimpleSelect.class);
  }

  @Test
  void givenSimpleSelect_whenGetFields_thenReturnFieldNames() {
    ElasticsearchSelect elasticsearchSelect = ElasticsearchSelect.of(selectBy("firstName", "lastName", "email"));

    List<String> fields = elasticsearchSelect.getFields();

    assertThat(fields).containsExactly("firstName", "lastName", "email");
  }

  @Test
  void givenSimpleSelectWithNestedFields_whenGetFields_thenReturnNestedFieldNames() {
    ElasticsearchSelect elasticsearchSelect = ElasticsearchSelect.of(selectBy("id", "address.city", "address.street"));

    List<String> fields = elasticsearchSelect.getFields();

    assertThat(fields).containsExactly("id", "address.city", "address.street");
  }

  @Test
  void givenSingleFieldSelect_whenGetFields_thenReturnSingleField() {
    ElasticsearchSelect elasticsearchSelect = ElasticsearchSelect.of(selectBy("id"));

    assertThat(elasticsearchSelect.getFields()).containsExactly("id");
  }

  @Test
  void givenUnsupportedSelect_whenOf_thenThrowIllegalArgumentException() {
    Select unsupportedSelect = new UnsupportedSelect();

    assertThatThrownBy(() -> ElasticsearchSelect.of(unsupportedSelect))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported select type");
  }

  private static class UnsupportedSelect implements Select {
    @Override
    public List<String> getPropertyNames() {
      return List.of();
    }
  }
}
