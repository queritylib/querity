package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.OrConditionsWrapper;

class ElasticsearchOrConditionsWrapper extends ElasticsearchLogicConditionsWrapper {
  public ElasticsearchOrConditionsWrapper(OrConditionsWrapper conditionsWrapper) {
    super(conditionsWrapper);
  }
}
