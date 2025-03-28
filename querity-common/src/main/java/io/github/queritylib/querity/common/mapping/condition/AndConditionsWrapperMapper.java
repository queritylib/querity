package io.github.queritylib.querity.common.mapping.condition;

import io.github.queritylib.querity.api.AndConditionsWrapper;
import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.common.mapping.PropertyNameMapper;

import java.util.stream.Collectors;

class AndConditionsWrapperMapper implements ConditionMapper<AndConditionsWrapper> {
  @Override
  public boolean canMap(Condition condition) {
    return AndConditionsWrapper.class.isAssignableFrom(condition.getClass());
  }

  @Override
  public AndConditionsWrapper mapCondition(AndConditionsWrapper condition, PropertyNameMapper propertyNameMapper) {
    return condition.toBuilder()
        .conditions(condition.getConditions().stream()
            .map(c -> ConditionMapperFactory.getConditionMapper(c)
                .mapCondition(c, propertyNameMapper))
            .collect(Collectors.toList()))
        .build();
  }
}
