package io.github.queritylib.querity.jpa;

import io.github.queritylib.querity.api.AdvancedQuery;
import io.github.queritylib.querity.api.NativeSelectWrapper;
import io.github.queritylib.querity.api.Select;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;
import java.util.Map;

/**
 * Factory for creating JPA queries from AdvancedQuery objects.
 * Handles projection queries with SELECT, GROUP BY, and HAVING clauses.
 *
 * @param <T> the entity type
 */
public class JpaAdvancedQueryFactory<T> {
  private final Class<T> entityClass;
  private final AdvancedQuery query;
  private final EntityManager entityManager;

  JpaAdvancedQueryFactory(Class<T> entityClass, AdvancedQuery query, EntityManager entityManager) {
    this.entityClass = entityClass;
    this.query = query;
    this.entityManager = entityManager;
  }

  /**
   * Create a JPA TypedQuery&lt;Tuple&gt; for projection queries.
   * The query will select only the specified fields and return them as a Tuple.
   *
   * @return A TypedQuery that can be executed to retrieve projected results.
   */
  public TypedQuery<Tuple> getJpaProjectionQuery() {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> cq = cb.createTupleQuery();
    Root<T> root = cq.from(entityClass);

    Metamodel metamodel = entityManager.getMetamodel();

    JpaQueryUtils.applyDistinct(cq, query);
    JpaQueryUtils.applyFilters(entityClass, metamodel, root, cq, cb, query);
    applyProjectionSelections(metamodel, cq, root, cb);
    applyGroupBy(metamodel, root, cq, cb);
    applyHaving(metamodel, root, cq, cb);
    // Note: ORDER BY is applied after GROUP BY/HAVING as per SQL standard.
    // This allows sorting by aggregated values or aliases defined in SELECT.
    JpaQueryUtils.applySorting(metamodel, root, cq, cb, query);

    TypedQuery<Tuple> tq = createTypedQuery(cq);

    JpaQueryUtils.applyPagination(tq, query);

    return tq;
  }

  /**
   * Execute a projection query and return results as a list of maps.
   *
   * @return List of maps containing the projected property values
   * @throws IllegalStateException if the query does not have a select clause
   */
  public List<Map<String, Object>> getProjectedResults() {
    if (query == null || !query.hasSelect()) {
      throw new IllegalStateException("Projection query requires a SELECT clause");
    }

    TypedQuery<Tuple> tq = getJpaProjectionQuery();
    JpaSelect jpaSelect = getJpaSelect(query.getSelect());
    List<String> aliasNames = jpaSelect.getPropertyNames();

    return tq.getResultList().stream()
        .map(tuple -> tupleToMap(tuple, aliasNames))
        .toList();
  }

  private Map<String, Object> tupleToMap(Tuple tuple, List<String> aliasNames) {
    java.util.LinkedHashMap<String, Object> map = new java.util.LinkedHashMap<>();
    for (int i = 0; i < aliasNames.size(); i++) {
      // Use the alias directly as the key - aliases are unique by design
      // (either explicitly set by the user or derived from property names)
      map.put(aliasNames.get(i), tuple.get(i));
    }
    return map;
  }

  /**
   * Apply projection selections to the CriteriaQuery.
   */
  private void applyProjectionSelections(Metamodel metamodel, CriteriaQuery<Tuple> cq, Root<T> root, CriteriaBuilder cb) {
    if (query != null && query.hasSelect()) {
      JpaSelect jpaSelect = getJpaSelect(query.getSelect());
      List<Selection<?>> selections = jpaSelect.toSelections(metamodel, root, cq, cb);
      cq.multiselect(selections);
    }
  }

  private JpaSelect getJpaSelect(Select select) {
    if (select instanceof NativeSelectWrapper<?> nativeSelectWrapper) {
      return JpaNativeSelectWrapper.of(nativeSelectWrapper);
    }
    return JpaSelect.of(select);
  }

  private void applyGroupBy(Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    if (query != null && query.hasGroupBy()) {
      JpaGroupBy jpaGroupBy = JpaGroupBy.of(query.getGroupBy());
      List<Expression<?>> groupByExpressions = jpaGroupBy.toExpressions(metamodel, root, cb);
      cq.groupBy(groupByExpressions);
    }
  }

  private void applyHaving(Metamodel metamodel, Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    if (query != null && query.hasHaving()) {
      Predicate havingPredicate = JpaQueryUtils.getPredicate(entityClass, query.getHaving(), metamodel, root, cq, cb);
      cq.having(havingPredicate);
    }
  }

  private <R> TypedQuery<R> createTypedQuery(CriteriaQuery<R> cq) {
    return entityManager.createQuery(cq);
  }
}
