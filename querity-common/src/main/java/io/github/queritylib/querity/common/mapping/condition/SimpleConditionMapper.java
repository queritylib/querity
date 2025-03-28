package io.github.queritylib.querity.common.mapping.condition;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.SimpleCondition;
import io.github.queritylib.querity.common.mapping.PropertyNameMapper;

class SimpleConditionMapper implements ConditionMapper<SimpleCondition> {
  @Override
  public boolean canMap(Condition condition) {
    return SimpleCondition.class.isAssignableFrom(condition.getClass());
  }

  @Override
  public SimpleCondition mapCondition(SimpleCondition condition, PropertyNameMapper propertyNameMapper) {
    return condition.toBuilder()
        .propertyName(propertyNameMapper.mapPropertyName(condition.getPropertyName()))
        .build();
  }
}
