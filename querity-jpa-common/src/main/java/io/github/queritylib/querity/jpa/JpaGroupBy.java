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
  @SuppressWarnings("unchecked")
  static JpaGroupBy of(GroupBy groupBy) {
    if (groupBy instanceof io.github.queritylib.querity.api.SimpleGroupBy simpleGroupBy) {
      return new JpaSimpleGroupBy(simpleGroupBy);
    }
    if (groupBy instanceof io.github.queritylib.querity.api.NativeGroupByWrapper<?> nativeGroupByWrapper) {
      Object unsupportedGrouping = nativeGroupByWrapper.getNativeGroupings().stream()
          .filter(grouping -> !(grouping instanceof GroupBySpecification))
          .findFirst()
          .orElse(null);
      if (unsupportedGrouping == null) {
        return new JpaNativeGroupByWrapper(
            (io.github.queritylib.querity.api.NativeGroupByWrapper<GroupBySpecification<?>>) groupBy);
      }
      throw new IllegalArgumentException(String.format(
          "NativeGroupByWrapper with type %s is not supported by the JPA module. " +
              "Use GroupBySpecification for deferred grouping expressions.",
          unsupportedGrouping.getClass().getSimpleName()));
    }
    throw new IllegalArgumentException("Unsupported GroupBy type: " + groupBy.getClass());
  }
}
