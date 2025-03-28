package io.github.queritylib.querity.common.mapping.condition;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.common.mapping.PropertyNameMapper;

public interface ConditionMapper<C extends Condition> {
  boolean canMap(Condition c);

  C mapCondition(C condition, PropertyNameMapper propertyNameMapper);
}
