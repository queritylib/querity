package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.OrConditionsWrapper;

class MongodbOrConditionsWrapper extends MongodbLogicConditionsWrapper {
  public MongodbOrConditionsWrapper(OrConditionsWrapper conditionsWrapper) {
    super(conditionsWrapper);
  }
}
