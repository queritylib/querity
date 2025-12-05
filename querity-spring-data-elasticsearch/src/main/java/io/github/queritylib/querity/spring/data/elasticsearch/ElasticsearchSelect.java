package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.Select;
import io.github.queritylib.querity.api.SimpleSelect;

import java.util.List;

abstract class ElasticsearchSelect {

  public abstract List<String> getFields();

  public static ElasticsearchSelect of(Select select) {
    if (select instanceof SimpleSelect simpleSelect) {
      return new ElasticsearchSimpleSelect(simpleSelect);
    }
    throw new IllegalArgumentException("Unsupported select type: " + select.getClass().getSimpleName());
  }
}
