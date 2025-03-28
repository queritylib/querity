package io.github.queritylib.querity.common.mapping.condition;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.OrConditionsWrapper;
import io.github.queritylib.querity.common.mapping.PropertyNameMapper;

import java.util.stream.Collectors;

class OrConditionsWrapperMapper implements ConditionMapper<OrConditionsWrapper> {
  @Override
  public boolean canMap(Condition condition) {
    return OrConditionsWrapper.class.isAssignableFrom(condition.getClass());
  }

  @Override
  public OrConditionsWrapper mapCondition(OrConditionsWrapper condition, PropertyNameMapper propertyNameMapper) {
    return condition.toBuilder()
        .conditions(condition.getConditions().stream()
            .map(c -> ConditionMapperFactory.getConditionMapper(c)
                .mapCondition(c, propertyNameMapper))
            .collect(Collectors.toList()))
        .build();
  }
}
