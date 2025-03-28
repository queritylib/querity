package io.github.queritylib.querity.common.mapping.condition;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.NotCondition;
import io.github.queritylib.querity.common.mapping.PropertyNameMapper;

class NotConditionMapper implements ConditionMapper<NotCondition> {
  @Override
  public boolean canMap(Condition condition) {
    return NotCondition.class.isAssignableFrom(condition.getClass());
  }

  @Override
  public NotCondition mapCondition(NotCondition condition, PropertyNameMapper propertyNameMapper) {
    Condition c = condition.getCondition();
    return condition.toBuilder()
        .condition(ConditionMapperFactory.getConditionMapper(c)
            .mapCondition(c, propertyNameMapper))
        .build();
  }
}
