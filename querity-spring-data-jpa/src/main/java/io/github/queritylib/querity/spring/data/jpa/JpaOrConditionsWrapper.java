package io.github.queritylib.querity.spring.data.jpa;

import io.github.queritylib.querity.api.OrConditionsWrapper;

class JpaOrConditionsWrapper extends JpaLogicConditionsWrapper {
  public JpaOrConditionsWrapper(OrConditionsWrapper conditionsWrapper) {
    super(conditionsWrapper);
  }
}
