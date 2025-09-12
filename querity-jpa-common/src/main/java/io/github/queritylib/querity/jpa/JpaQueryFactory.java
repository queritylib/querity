package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.Condition;
import io.github.queritylib.querity.api.Pagination;
import io.github.queritylib.querity.api.Query;
import io.github.queritylib.querity.api.Sort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;
import java.util.stream.Stream;

public class JpaQueryFactory<T> {
  private final Class<T> entityClass;
  private final Query query;
  private final EntityManager entityManager;

  JpaQueryFactory(Class<T> entityClass, Query query, EntityManager entityManager) {
    this.entityClass = entityClass;
    this.query = query;
    this.entityManager = entityManager;
  }

  /**
   * Create a JPA TypedQuery&lt;Tuple&gt; based on the provided Query object.
   * The query will include filters, sorting, selections, and pagination as specified in the Query.
   * The tuple contains the root entity and the additional fields for sorting;
   * the root entity is always included as the first element of the tuple.
   *
   * @return A TypedQuery that can be executed to retrieve results.
   */
  public TypedQuery<Tuple> getJpaQuery() {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    // Use Tuple query to support sorting by nested fields when using distinct
    CriteriaQuery<Tuple> cq = cb.createTupleQuery();
    Root<T> root = cq.from(entityClass);

    Metamodel metamodel = entityManager.getMetamodel();

    applyDistinct(cq);
    applyFilters(metamodel, root, cq, cb);
    applySorting(metamodel, root, cq, cb);
    applySelections(cq, root);

    TypedQuery<Tuple> tq = createTypedQuery(cq);

    applyPagination(tq);

    return tq;
  }

  /**
   * Apply selections to the CriteriaQuery.
   * If there are sorting orders, they are added as selections to ensure they are included in the result set.
   * The root entity is always included as the first selection.
   */
  private void applySelections(CriteriaQuery<Tuple> cq, Root<T> root) {
    List<Selection<?>> selections = Stream.concat(
            Stream.of(root),
            cq.getOrderList().stream()
                .<Selection<?>>map(Order::getExpression))
        .toList();
    cq.multiselect(selections);
  }

  private void applyDistinct(CriteriaQuery<?> cq) {
    if (query != null && query.isDistinct())
      cq.distinct(true);
  }

  public TypedQuery<Long> getJpaCountQuery() {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<T> root = cq.from(entityClass);

    Metamodel metaModel = entityManager.getMetamodel();

    applySelectCount(root, cq, cb);
    applyFilters(metaModel, root, cq, cb);

    return createTypedQuery(cq);
  }

  private void applySelectCount(Root<T> root, CriteriaQuery<Long> cq, CriteriaBuilder cb) {
    cq.select(cb.countDistinct(root)); // always counting distinct rows, should not be a problem since there's no sorting
  }

  private void applyFilters(Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    if (query != null && query.hasFilter())
      cq.where(getPredicate(entityClass, query.getFilter(), metamodel, root, cq, cb));
  }

  private static <T> Predicate getPredicate(Class<T> entityClass, Condition filter, Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    return JpaCondition.of(filter).toPredicate(entityClass, metamodel, root, cq, cb);
  }

  private void applySorting(Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    if (query != null && query.hasSort())
      cq.orderBy(getOrders(query.getSort(), metamodel, root, cb));
  }

  private static <T> List<Order> getOrders(List<Sort> sort, Metamodel metamodel, Root<T> root, CriteriaBuilder cb) {
    return sort.stream()
        .map(JpaSort::new)
        .map(jpaSort -> jpaSort.toOrder(metamodel, root, cb))
        .toList();
  }

  private void applyPagination(TypedQuery<?> tq) {
    if (query != null && query.hasPagination())
      applyPagination(query.getPagination(), tq);
  }

  private static <T> void applyPagination(Pagination pagination, TypedQuery<T> tq) {
    tq.setMaxResults(pagination.getPageSize())
        .setFirstResult(pagination.getPageSize() * (pagination.getPage() - 1));
  }

  private <R> TypedQuery<R> createTypedQuery(CriteriaQuery<R> cq) {
    return entityManager.createQuery(cq);
  }
}
