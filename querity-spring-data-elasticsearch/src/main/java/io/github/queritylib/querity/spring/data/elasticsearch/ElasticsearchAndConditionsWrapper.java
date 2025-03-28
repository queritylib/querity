package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.AndConditionsWrapper;

class ElasticsearchAndConditionsWrapper extends ElasticsearchLogicConditionsWrapper {
  public ElasticsearchAndConditionsWrapper(AndConditionsWrapper conditionsWrapper) {
    super(conditionsWrapper);
  }
}
