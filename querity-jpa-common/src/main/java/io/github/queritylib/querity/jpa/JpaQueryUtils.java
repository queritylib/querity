package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.Pagination;
import io.github.queritylib.querity.api.QueryDefinition;
import io.github.queritylib.querity.api.Sort;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;

/**
 * Utility class containing common JPA query operations shared between
 * {@link JpaQueryFactory} and {@link JpaAdvancedQueryFactory}.
 */
public final class JpaQueryUtils {

  private JpaQueryUtils() {
    // Utility class - prevent instantiation
  }

  /**
   * Apply distinct to the criteria query if the query definition specifies it.
   *
   * @param cq    the criteria query
   * @param query the query definition
   */
  public static void applyDistinct(CriteriaQuery<?> cq, QueryDefinition query) {
    if (query != null && query.isDistinct()) {
      cq.distinct(true);
    }
  }

  /**
   * Apply filter conditions to the criteria query.
   *
   * @param entityClass the entity class
   * @param metamodel   the JPA metamodel
   * @param root        the query root
   * @param cq          the criteria query
   * @param cb          the criteria builder
   * @param query       the query definition
   * @param <T>         the entity type
   */
  public static <T> void applyFilters(Class<T> entityClass, Metamodel metamodel, Root<T> root,
                                       CriteriaQuery<?> cq, CriteriaBuilder cb, QueryDefinition query) {
    if (query != null && query.hasFilter()) {
      cq.where(getPredicate(entityClass, query.getFilter(), metamodel, root, cq, cb));
    }
  }

  /**
   * Apply sorting to the criteria query.
   *
   * @param metamodel the JPA metamodel
   * @param root      the query root
   * @param cq        the criteria query
   * @param cb        the criteria builder
   * @param query     the query definition
   * @param <T>       the entity type
   */
  public static <T> void applySorting(Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq,
                                       CriteriaBuilder cb, QueryDefinition query) {
    if (query != null && query.hasSort()) {
      cq.orderBy(getOrders(query.getSort(), metamodel, root, cb));
    }
  }

  /**
   * Apply pagination to the typed query.
   *
   * @param tq    the typed query
   * @param query the query definition
   */
  public static void applyPagination(TypedQuery<?> tq, QueryDefinition query) {
    if (query != null && query.hasPagination()) {
      applyPagination(query.getPagination(), tq);
    }
  }

  /**
   * Apply pagination settings to the typed query.
   *
   * @param pagination the pagination settings
   * @param tq         the typed query
   * @param <T>        the result type
   */
  public static <T> void applyPagination(Pagination pagination, TypedQuery<T> tq) {
    tq.setMaxResults(pagination.getPageSize())
        .setFirstResult(pagination.getPageSize() * (pagination.getPage() - 1));
  }

  /**
   * Convert a filter condition to a JPA Predicate.
   *
   * @param entityClass the entity class
   * @param filter      the filter condition
   * @param metamodel   the JPA metamodel
   * @param root        the query root
   * @param cq          the criteria query
   * @param cb          the criteria builder
   * @param <T>         the entity type
   * @return the JPA Predicate
   */
  public static <T> Predicate getPredicate(Class<T> entityClass, Condition filter, Metamodel metamodel,
                                            Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return JpaCondition.of(filter).toPredicate(entityClass, metamodel, root, cq, cb);
  }

  /**
   * Convert sort specifications to JPA Order list.
   *
   * @param sort      the sort specifications
   * @param metamodel the JPA metamodel
   * @param root      the query root
   * @param cb        the criteria builder
   * @param <T>       the entity type
   * @return list of JPA Orders
   */
  public static <T> List<Order> getOrders(List<Sort> sort, Metamodel metamodel, Root<T> root, CriteriaBuilder cb) {
    return sort.stream()
        .map(JpaSort::of)
        .map(jpaSort -> jpaSort.toOrder(metamodel, root, cb))
        .toList();
  }
}
