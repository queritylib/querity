package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.LogicConditionsWrapper;
import io.github.queritylib.querity.api.LogicOperator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.experimental.Delegate;

abstract class JpaLogicConditionsWrapper extends JpaCondition {
  @Delegate
  private final LogicConditionsWrapper conditionsWrapper;

  protected JpaLogicConditionsWrapper(LogicConditionsWrapper conditionsWrapper) {
    this.conditionsWrapper = conditionsWrapper;
  }

  @Override
  public <T> Predicate toPredicate(Class<T> entityClass, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return getLogicPredicate(getConditionPredicates(entityClass, root, cq, cb), cb);
  }

  private <T> Predicate[] getConditionPredicates(Class<T> entityClass, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return getConditions().stream()
        .map(JpaCondition::of)
        .map(c -> c.toPredicate(entityClass, root, cq, cb))
        .toArray(Predicate[]::new);
  }

  private Predicate getLogicPredicate(Predicate[] conditionPredicates, CriteriaBuilder cb) {
    return getLogic().equals(LogicOperator.AND) ?
        cb.and(conditionPredicates) :
        cb.or(conditionPredicates);
  }
}
