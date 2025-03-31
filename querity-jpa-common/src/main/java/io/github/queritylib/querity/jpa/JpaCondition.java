package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Condition;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Set;

import static io.github.queritylib.querity.common.util.ConditionUtils.getConditionImplementation;
import static io.github.queritylib.querity.common.util.ReflectionUtils.findSubclasses;

abstract class JpaCondition {

  public abstract <T> Predicate toPredicate(Class<T> entityClass, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb);

  private static final Set<Class<? extends JpaCondition>> JPA_CONDITION_IMPLEMENTATIONS = findSubclasses(JpaCondition.class);

  public static JpaCondition of(Condition condition) {
    return getConditionImplementation(JPA_CONDITION_IMPLEMENTATIONS, condition)
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Condition class %s is not supported by the JPA module", condition.getClass().getSimpleName())));
  }
}
