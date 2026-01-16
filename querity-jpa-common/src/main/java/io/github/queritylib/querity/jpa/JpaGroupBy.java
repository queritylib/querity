package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.GroupBy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;

/**
 * Interface for JPA GROUP BY implementations.
 */
public interface JpaGroupBy {

  /**
   * Convert to JPA expressions for GROUP BY clause.
   *
   * @param metamodel the JPA metamodel
   * @param root the root entity
   * @param cb the criteria builder
   * @return list of expressions for grouping
   */
  List<Expression<?>> toExpressions(Metamodel metamodel, Root<?> root, CriteriaBuilder cb);

  /**
   * Factory method to create a JpaGroupBy from a GroupBy.
   *
   * @param groupBy the GroupBy to convert
   * @return a JpaGroupBy implementation
   */
  static JpaGroupBy of(GroupBy groupBy) {
    if (groupBy instanceof io.github.queritylib.querity.api.SimpleGroupBy simpleGroupBy) {
      return new JpaSimpleGroupBy(simpleGroupBy);
    }
    throw new IllegalArgumentException("Unsupported GroupBy type: " + groupBy.getClass());
  }
}
