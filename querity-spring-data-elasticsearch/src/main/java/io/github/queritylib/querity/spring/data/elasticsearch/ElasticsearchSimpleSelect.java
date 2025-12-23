package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.PropertyExpression;
import io.github.queritylib.querity.api.SimpleSelect;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

class ElasticsearchSimpleSelect extends ElasticsearchSelect {
  @Delegate
  private final SimpleSelect simpleSelect;

  public ElasticsearchSimpleSelect(SimpleSelect simpleSelect) {
    this.simpleSelect = simpleSelect;
  }

  @Override
  public List<String> getFields() {
    if (simpleSelect.hasExpressions()) {
      List<String> fields = new ArrayList<>();
      for (PropertyExpression expr : simpleSelect.getExpressions()) {
        // This will throw UnsupportedOperationException for functions
        fields.add(ElasticsearchFunctionMapper.getFieldName(expr));
      }
      return fields;
    }
    return getPropertyNames();
  }
}
