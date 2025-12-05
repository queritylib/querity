package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.SimpleSelect;

import java.util.List;

abstract class ElasticsearchSelect {

  public abstract List<String> getFields();

  public static ElasticsearchSelect of(SimpleSelect select) {
    return new ElasticsearchSimpleSelect(select);
  }
}
