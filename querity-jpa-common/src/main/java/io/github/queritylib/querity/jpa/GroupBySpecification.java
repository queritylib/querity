package io.github.queritylib.querity.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * Functional interface for creating JPA grouping Expression instances.
 * Similar to {@code OrderSpecification} and {@code SelectionSpecification} but for
 * GROUP BY terms instead of sorting or projections.
 * <p>
 * This allows users to create native JPA groupings using a lambda that receives
 * the Root and CriteriaBuilder at query execution time.
 *
 * @param <T> the entity type
 */
@FunctionalInterface
public interface GroupBySpecification<T> {
  /**
   * Creates an Expression to group by for the given root and criteria builder.
   *
   * @param root the root of the query
   * @param cb   the criteria builder
   * @return the Expression to include in the GROUP BY clause
   */
  @SuppressWarnings("java:S1452")
  Expression<?> toExpression(Root<T> root, CriteriaBuilder cb);
}
