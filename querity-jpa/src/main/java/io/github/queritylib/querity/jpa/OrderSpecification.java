package io.github.queritylib.querity.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

/**
 * Functional interface for creating JPA Order instances.
 * Similar to {@link Specification} but for sorting instead of filtering.
 * <p>
 * This allows users to create native JPA sorts using a lambda that receives
 * the Root and CriteriaBuilder at query execution time.
 *
 * @param <T> the entity type
 */
@FunctionalInterface
public interface OrderSpecification<T> {
  /**
   * Creates an Order for the given root and criteria builder.
   *
   * @param root the root of the query
   * @param cb   the criteria builder
   * @return the Order to apply
   */
  Order toOrder(Root<T> root, CriteriaBuilder cb);
}

