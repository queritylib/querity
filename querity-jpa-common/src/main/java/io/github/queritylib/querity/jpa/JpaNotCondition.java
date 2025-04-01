package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.NotCondition;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;
import lombok.experimental.Delegate;

class JpaNotCondition extends JpaCondition {
  @Delegate
  private final NotCondition notCondition;

  public JpaNotCondition(NotCondition notCondition) {
    this.notCondition = notCondition;
  }

  @Override
  public <T> Predicate toPredicate(Class<T> entityClass, Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return cb.not(
        cb.and( // work-around to make double-negation work (regression on Hibernate 6)
            JpaCondition.of(getCondition()).toPredicate(entityClass, metamodel, root, cq, cb)));
  }
}
