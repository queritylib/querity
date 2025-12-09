package io.github.queritylib.querity.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

/**
 * Functional interface for creating JPA Selection instances.
 * Similar to {@code OrderSpecification} but for projections instead of sorting.
 * <p>
 * This allows users to create native JPA selections using a lambda that receives
 * the Root and CriteriaBuilder at query execution time.
 *
 * @param <T> the entity type
 */
@FunctionalInterface
public interface SelectionSpecification<T> {
  /**
   * Creates a Selection for the given root and criteria builder.
   *
   * @param root the root of the query
   * @param cb   the criteria builder
   * @return the Selection to include in the projection
   */
  Selection<?> toSelection(Root<T> root, CriteriaBuilder cb);

  /**
   * Returns the alias for this selection.
   * Override this method to provide a custom alias for the projected field.
   *
   * @return the alias, or null to use the default
   */
  default String getAlias() {
    return null;
  }
}
