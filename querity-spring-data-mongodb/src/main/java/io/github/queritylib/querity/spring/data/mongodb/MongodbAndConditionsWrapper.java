package io.github.queritylib.querity.spring.data.mongodb;

import io.github.queritylib.querity.api.AndConditionsWrapper;

class MongodbAndConditionsWrapper extends MongodbLogicConditionsWrapper {
  public MongodbAndConditionsWrapper(AndConditionsWrapper conditionsWrapper) {
    super(conditionsWrapper);
  }
}
