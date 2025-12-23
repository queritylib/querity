package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Function;
import io.github.queritylib.querity.api.FunctionCall;
import io.github.queritylib.querity.api.PropertyReference;
import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;
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

  @Test
  void givenSimpleSelectWithExpressions_whenGetFields_thenReturnFieldNames() {
    SimpleSelect simpleSelect = SimpleSelect.ofExpressions(
        PropertyReference.of("firstName"),
        PropertyReference.of("lastName")
    );
    ElasticsearchSelect elasticsearchSelect = ElasticsearchSelect.of(simpleSelect);

    List<String> fields = elasticsearchSelect.getFields();

    assertThat(fields).containsExactly("firstName", "lastName");
  }

  @Test
  void givenSimpleSelectWithFunctionExpression_whenGetFields_thenThrowUnsupportedOperationException() {
    FunctionCall upperName = FunctionCall.of(Function.UPPER, PropertyReference.of("name"));
    SimpleSelect simpleSelect = SimpleSelect.ofExpressions(upperName);
    ElasticsearchSelect elasticsearchSelect = ElasticsearchSelect.of(simpleSelect);

    assertThatThrownBy(() -> elasticsearchSelect.getFields())
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("Function UPPER is not supported in Elasticsearch");
  }

  private static class UnsupportedSelect implements Select {
    @Override
    public List<String> getPropertyNames() {
      return List.of();
    }
  }
}
