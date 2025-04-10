package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.SimpleCondition;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import lombok.experimental.Delegate;

class JpaSimpleCondition extends JpaCondition {
  @Delegate
  private final SimpleCondition condition;

  JpaSimpleCondition(SimpleCondition condition) {
    this.condition = condition;
  }

  @Override
  public <T> Predicate toPredicate(Class<T> entityClass, Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return JpaOperatorMapper.getPredicate(entityClass, condition, metamodel, root, cb);
  }
}
