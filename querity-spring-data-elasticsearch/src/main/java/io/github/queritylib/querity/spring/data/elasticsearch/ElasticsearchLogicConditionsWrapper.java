package io.github.queritylib.querity.spring.data.elasticsearch;

import io.github.queritylib.querity.api.LogicConditionsWrapper;
import io.github.queritylib.querity.api.LogicOperator;
import lombok.experimental.Delegate;
import org.springframework.data.elasticsearch.core.query.Criteria;

abstract class ElasticsearchLogicConditionsWrapper extends ElasticsearchCondition {
  @Delegate
  private final LogicConditionsWrapper conditionsWrapper;

  protected ElasticsearchLogicConditionsWrapper(LogicConditionsWrapper conditionsWrapper) {
    this.conditionsWrapper = conditionsWrapper;
  }

  @Override
  public <T> Criteria toCriteria(Class<T> entityClass, boolean negate) {
    Criteria[] conditionsCriteria = buildConditionsCriteria(entityClass, negate);
    Criteria criteria = getLogic().equals(LogicOperator.AND) ^ negate ? // xor
        Criteria.and() :
        Criteria.or();
    for (Criteria condition : conditionsCriteria) {
      criteria = criteria.subCriteria(condition);
    }
    return criteria;
  }

  private <T> Criteria[] buildConditionsCriteria(Class<T> entityClass, boolean negate) {
    return getConditions().stream()
        .map(ElasticsearchCondition::of)
        .map(c -> c.toCriteria(entityClass, negate))
        .toArray(Criteria[]::new);
  }
}
