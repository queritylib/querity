package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.AndConditionsWrapper;

class JpaAndConditionsWrapper extends JpaLogicConditionsWrapper {
  public JpaAndConditionsWrapper(AndConditionsWrapper conditionsWrapper) {
    super(conditionsWrapper);
  }
}
