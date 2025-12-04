package io.github.queritylib.querity.common.mapping;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.api.QueryPreprocessor;
import io.github.queritylib.querity.api.SimpleSort;
import io.github.queritylib.querity.api.Sort;
import io.github.queritylib.querity.common.mapping.condition.ConditionMapperFactory;

public class PropertyNameMappingPreprocessor implements QueryPreprocessor {

  private final PropertyNameMapper propertyNameMapper;

  public PropertyNameMappingPreprocessor(PropertyNameMapper propertyNameMapper) {
    this.propertyNameMapper = propertyNameMapper;
  }

  @Override
  public Query preprocess(Query query) {
    return query.toBuilder()
        .filter(mapCondition(query.getFilter()))
        .sort(query.getSort().stream()
            .map(this::mapSort)
            .toArray(Sort[]::new))
        .build();
  }

  private Condition mapCondition(Condition condition) {
    if (condition == null) return null;
    return ConditionMapperFactory.getConditionMapper(condition)
        .mapCondition(condition, propertyNameMapper);
  }

  private Sort mapSort(Sort sort) {
    if (sort instanceof SimpleSort simpleSort) {
      return simpleSort.toBuilder()
          .propertyName(propertyNameMapper.mapPropertyName(simpleSort.getPropertyName()))
          .build();
    }
    // NativeSortWrapper and other Sort implementations are returned as-is
    return sort;
  }
}
