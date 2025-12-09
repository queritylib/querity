package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.SimpleSelect;
import lombok.experimental.Delegate;

import java.util.List;

class ElasticsearchSimpleSelect extends ElasticsearchSelect {
  @Delegate
  private final SimpleSelect simpleSelect;

  public ElasticsearchSimpleSelect(SimpleSelect simpleSelect) {
    this.simpleSelect = simpleSelect;
  }

  @Override
  public List<String> getFields() {
    return getPropertyNames();
  }
}
